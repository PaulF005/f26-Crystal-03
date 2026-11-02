package com.crystal.kip.service.curation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;

class QuestionCuratorTest {

    private final QuestionCurator curator = new QuestionCurator();

    private List<QuestionCurator.Candidate> bank() {
        List<QuestionCurator.Candidate> candidates = new ArrayList<>();
        for (long id = 1; id <= 25; id++) candidates.add(new QuestionCurator.Candidate(id, 201));
        candidates.add(new QuestionCurator.Candidate(26, 202));
        return candidates;
    }

    @Test
    void selectsTenDistinctQuestionsFromOnlyTheRequestedConcept() {
        var candidates = bank();
        var original = List.copyOf(candidates);
        var selected = curator.select(candidates, 201, Map.of());
        assertEquals(10, selected.size());
        assertEquals(10, new HashSet<>(selected).size());
        assertTrue(selected.stream().allMatch(id -> id >= 1 && id <= 25));
        assertEquals(original, candidates);
    }

    @Test
    void missedQuestionOutranksUnseenWhichOutranksFrequentlyCorrect() {
        var candidates = List.of(
            new QuestionCurator.Candidate(1, 201),
            new QuestionCurator.Candidate(2, 201),
            new QuestionCurator.Candidate(3, 201)
        );
        var history = Map.of(1L, new QuestionCurator.History(5, 0, 5), 3L, new QuestionCurator.History(10, 10, 0));
        assertEquals(List.of(1L, 2L, 3L), curator.select(candidates, 201, history, 3));
    }

    @Test
    void playerHistoriesStayIndependentAndUnmodified() {
        var candidates = List.of(new QuestionCurator.Candidate(1, 201), new QuestionCurator.Candidate(2, 201));
        var first = new HashMap<>(Map.of(1L, new QuestionCurator.History(5, 0, 5)));
        var second = Map.of(2L, new QuestionCurator.History(5, 0, 5));
        assertEquals(List.of(1L), curator.select(candidates, 201, first, 1));
        assertEquals(List.of(2L), curator.select(candidates, 201, second, 1));
        assertEquals(1, first.size());
        assertEquals(new QuestionCurator.History(5, 0, 5), first.get(1L));
    }

    @Test
    void tiedQuestionsAreRepeatableWithSameSeedAndVaryWithOtherSeeds() {
        var first = curator.select(bank(), 201, Map.of(), 10, new Random(1));
        assertEquals(first, curator.select(bank(), 201, Map.of(), 10, new Random(1)));
        assertNotEquals(first, curator.select(bank(), 201, Map.of(), 10, new Random(2)));
    }

    @Test
    void rejectsDuplicateIdsAndInsufficientBanks() {
        var duplicate = List.of(new QuestionCurator.Candidate(1, 201), new QuestionCurator.Candidate(1, 201));
        assertThrows(IllegalArgumentException.class, () -> curator.select(duplicate, 201, Map.of(), 1));
        assertThrows(IllegalArgumentException.class, () -> curator.select(bank(), 202, Map.of()));
        assertThrows(IllegalArgumentException.class, () -> curator.select(bank(), 201, Map.of(), 0));
    }

    @Test
    void rejectsInvalidCountsAndHandlesUnansweredDisplays() {
        assertThrows(IllegalArgumentException.class, () -> new QuestionCurator.History(1, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new QuestionCurator.History(-1, 0, 0));
        assertThrows(IllegalArgumentException.class, () ->
            new QuestionCurator.History(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE)
        );
        assertTrue(Double.isFinite(curator.priority(new QuestionCurator.History(3, 0, 0))));
    }
}
