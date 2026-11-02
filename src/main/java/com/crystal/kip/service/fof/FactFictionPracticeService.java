package com.crystal.kip.service.fof;

import com.crystal.kip.repository.fof.FactFictionRepository;
import com.crystal.kip.service.curation.QuestionCurator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Prepares questions for the authenticated player without counting them as displayed. */
@Service
public class FactFictionPracticeService {

    public record Choice(long id, String text) {}

    // Correct answers and explanations are deliberately absent from the question prompt.
    public record Prompt(long questionId, String text, List<Choice> choices) {}

    private final FactFictionRepository repository;
    private final CurrentPlayerService player;
    private final QuestionCurator curator;

    public FactFictionPracticeService(FactFictionRepository repository, CurrentPlayerService player, QuestionCurator curator) {
        this.repository = repository;
        this.player = player;
        this.curator = curator;
    }

    @Transactional(readOnly = true)
    public List<Prompt> prepare(long gameId, long topicId, long conceptId) {
        if (gameId <= 0 || topicId <= 0 || conceptId <= 0) throw new IllegalArgumentException("IDs must be positive");
        long profileId = player.id();
        var bank = repository.bank(gameId, topicId, conceptId);
        Map<Long, QuestionCurator.History> history = new HashMap<>();
        repository
            .history(profileId)
            .forEach((id, counts) -> history.put(id, new QuestionCurator.History(counts.shown(), counts.correct(), counts.incorrect())));
        var candidates = bank
            .stream()
            .map(q -> new QuestionCurator.Candidate(q.id(), q.conceptId()))
            .toList();
        var selectedIds = curator.select(candidates, conceptId, history);
        var byId = bank.stream().collect(Collectors.toMap(q -> q.id(), Function.identity()));
        return selectedIds
            .stream()
            .map(id -> {
                var question = byId.get(id);
                return new Prompt(
                    question.id(),
                    question.prompt(),
                    List.of(new Choice(question.trueAnswerId(), "True"), new Choice(question.falseAnswerId(), "False"))
                );
            })
            .toList();
    }
}
