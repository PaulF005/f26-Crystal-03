package com.crystal.kip.service.curation;

import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.StageAttempt;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import org.springframework.stereotype.Service;

/** Selects ten FOF scenarios using the current player's completed-session attempts. */
@Service
public class QuestionCurator {

    public List<Scenario> select(List<Scenario> scenarios, List<StageAttempt> attempts) {
        return select(scenarios, attempts, 10, new Random());
    }

    // Tests can supply a smaller session size and repeatable random ordering.
    List<Scenario> select(List<Scenario> scenarios, List<StageAttempt> attempts, int count, Random random) {
        Objects.requireNonNull(scenarios, "scenarios");
        Objects.requireNonNull(attempts, "attempts");
        Objects.requireNonNull(random, "random");
        if (count < 1 || scenarios.size() < count) {
            throw new IllegalArgumentException("There must be enough scenarios for the requested session size");
        }

        Map<Long, History> historyByStage = summarizeHistory(attempts);
        var scenarioIds = new HashSet<Long>();
        var stageIds = new HashSet<Long>();
        List<Ranked> ranked = new ArrayList<>();
        for (Scenario scenario : scenarios) {
            Long scenarioId = Objects.requireNonNull(scenario.getId(), "scenario ID");
            Long stageId = Objects.requireNonNull(scenario.getStartingStage().getId(), "starting stage ID");
            if (!scenarioIds.add(scenarioId) || !stageIds.add(stageId)) {
                throw new IllegalArgumentException("FOF scenarios and their starting stages must be distinct");
            }
            History history = historyByStage.getOrDefault(stageId, new History());
            ranked.add(new Ranked(scenario, history.total() == 0, history.priority(), history.lastAnsweredAt));
        }

        // Shuffle first so exact ties vary between sessions.
        Collections.shuffle(ranked, random);
        ranked.sort(
            Comparator.comparing(Ranked::unanswered)
                .reversed()
                .thenComparing(Comparator.comparingDouble(Ranked::priority).reversed())
                .thenComparing(Ranked::lastAnsweredAt, Comparator.nullsFirst(Comparator.naturalOrder()))
        );
        return ranked.subList(0, count).stream().map(Ranked::scenario).toList();
    }

    private Map<Long, History> summarizeHistory(List<StageAttempt> attempts) {
        Map<Long, History> historyByStage = new HashMap<>();
        for (StageAttempt attempt : attempts) {
            Long stageId = Objects.requireNonNull(attempt.getStage().getId(), "attempt stage ID");
            Instant answeredAt = Objects.requireNonNull(attempt.getAnsweredAt(), "answer time");
            History history = historyByStage.computeIfAbsent(stageId, id -> new History());
            if (Objects.requireNonNull(attempt.getCorrect(), "answer correctness")) {
                history.correct++;
            } else {
                history.incorrect++;
            }
            if (history.lastAnsweredAt == null || answeredAt.isAfter(history.lastAnsweredAt)) {
                history.lastAnsweredAt = answeredAt;
            }
        }
        return historyByStage;
    }

    private static class History {

        long correct;
        long incorrect;
        Instant lastAnsweredAt;

        long total() {
            return correct + incorrect;
        }

        double priority() {
            if (total() == 0) return 0.0;
            double errorRate = (double) incorrect / total();
            return 4.0 * errorRate + 2.0 / (total() + 1.0);
        }
    }

    private record Ranked(Scenario scenario, boolean unanswered, double priority, Instant lastAnsweredAt) {}
}
