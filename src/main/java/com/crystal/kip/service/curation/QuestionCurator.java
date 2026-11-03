package com.crystal.kip.service.curation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import org.springframework.stereotype.Service;

/** Selects questions from a supplied FOF bank using one player's history. */
@Service
public class QuestionCurator {

    public record Candidate(long questionId, long conceptId) {
        public Candidate {
            if (questionId <= 0 || conceptId <= 0) {
                throw new IllegalArgumentException("Question and concept IDs must be positive");
            }
        }
    }

    public record History(int timesShown, int timesCorrect, int timesIncorrect) {
        public History {
            if (timesShown < 0 || timesCorrect < 0 || timesIncorrect < 0 || (long) timesCorrect + timesIncorrect > timesShown) {
                throw new IllegalArgumentException("History counts must be nonnegative; answers cannot exceed displays");
            }
        }
    }

    /** Default practice session: ten distinct questions from the selected concept. */
    public List<Long> select(List<Candidate> bank, long conceptId, Map<Long, History> playerHistory) {
        return select(bank, conceptId, playerHistory, 10);
    }

    public List<Long> select(List<Candidate> bank, long conceptId, Map<Long, History> playerHistory, int count) {
        return select(bank, conceptId, playerHistory, count, new Random());
    }

    // Supplying a seeded Random makes tie handling repeatable in tests.
    List<Long> select(List<Candidate> bank, long conceptId, Map<Long, History> playerHistory, int count, Random random) {
        Objects.requireNonNull(bank, "bank");
        Objects.requireNonNull(playerHistory, "playerHistory");
        Objects.requireNonNull(random, "random");
        if (conceptId <= 0 || count < 1) throw new IllegalArgumentException("Concept ID and count must be positive");

        List<Ranked> eligible = new ArrayList<>();
        var seenIds = new HashSet<Long>();
        for (Candidate question : bank) {
            if (!seenIds.add(question.questionId())) {
                throw new IllegalArgumentException("Duplicate question ID: " + question.questionId());
            }
            if (question.conceptId() != conceptId) continue;
            History history = playerHistory.getOrDefault(question.questionId(), new History(0, 0, 0));
            eligible.add(new Ranked(question.questionId(), priority(history)));
        }
        if (eligible.size() < count) {
            throw new IllegalArgumentException(
                "Concept " + conceptId + " needs " + count + " questions, but only " + eligible.size() + " are available"
            );
        }

        // Shuffle before a stable sort so equally ranked questions vary between sessions.
        Collections.shuffle(eligible, random);
        eligible.sort(Comparator.comparingDouble(Ranked::priority).reversed());
        return eligible.subList(0, count).stream().map(Ranked::questionId).toList();
    }

    /** Starting policy; higher values receive priority. This is not a mastery score. */
    public double priority(History history) {
        Objects.requireNonNull(history, "history");
        if (history.timesShown() == 0) return 3.0;
        long answered = (long) history.timesCorrect() + history.timesIncorrect();
        double errorRate = answered == 0 ? 0.0 : (double) history.timesIncorrect() / answered;
        return 4.0 * errorRate + 2.0 / (history.timesShown() + 1.0);
    }

    private record Ranked(long questionId, double priority) {}
}
