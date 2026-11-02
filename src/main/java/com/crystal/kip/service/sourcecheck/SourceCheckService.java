package com.crystal.kip.service.sourcecheck;

import com.crystal.kip.repository.sourcecheck.SourceCheckRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Service;

/** Reads registered URLs from the database; fingerprints remain in files. */
@Service
public class SourceCheckService {

    public record Report(long sourceId, SourceChecker.Result result, List<Long> affectedQuestionIds, String error) {}

    private final SourceCheckRepository repository;
    private final SourceChecker checker;

    public SourceCheckService(SourceCheckRepository repository, SourceChecker checker) {
        this.repository = repository;
        this.checker = checker;
    }

    // Network work deliberately does not hold a database transaction open.
    public List<Report> checkAll() {
        var reports = new ArrayList<Report>();
        var checkedUrls = new HashMap<String, SourceChecker.Result>();
        for (var source : repository.sources()) {
            try {
                // Read links before changing a fingerprint so a failed link lookup cannot hide a change.
                var linked = repository.affectedQuestions(source.sourceId());
                var result = checkedUrls.get(source.url());
                if (result == null) {
                    result = checker.checkSource(source.url());
                    checkedUrls.put(source.url(), result);
                }
                var affected = result.status() == SourceChecker.Status.CHANGED ? linked : List.<Long>of();
                reports.add(new Report(source.sourceId(), result, affected, null));
            } catch (Exception e) {
                reports.add(new Report(source.sourceId(), null, List.of(), e.toString()));
            }
        }
        return List.copyOf(reports);
    }
}
