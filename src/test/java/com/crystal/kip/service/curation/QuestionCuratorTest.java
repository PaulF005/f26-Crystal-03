package com.crystal.kip.service.curation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.crystal.kip.domain.Question;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Stage;
import com.crystal.kip.domain.StageAttempt;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class QuestionCuratorTest {

    private final QuestionCurator curator = new QuestionCurator();
    private static final Instant OLDER = Instant.parse("2026-10-01T00:00:00Z");
    private static final Instant NEWER = Instant.parse("2026-11-01T00:00:00Z");

    private List<Scenario> bank(int count) {
        List<Scenario> scenarios = new ArrayList<>();
        for (long id = 1; id <= count; id++) {
            Scenario scenario = new Scenario().id(id);
            Stage stage = new Stage()
                .id(1000 + id)
                .scenario(scenario)
                .question(new Question().id(2000 + id));
            scenario.startingStage(stage).addStage(stage);
            scenarios.add(scenario);
        }
        return scenarios;
    }

    private StageAttempt attempt(Scenario scenario, boolean correct, Instant time) {
        return new StageAttempt().stage(scenario.getStartingStage()).correct(correct).answeredAt(time);
    }

    @Test
    void coversAllSeventyFiveAcrossEightCompletedSessions() {
        var scenarios = bank(75);
        List<StageAttempt> history = new ArrayList<>();
        var covered = new HashSet<Long>();
        for (int session = 0; session < 8; session++) {
            var selected = curator.select(scenarios, history);
            assertEquals(10, selected.size());
            assertEquals(10, selected.stream().map(Scenario::getId).distinct().count());
            long unanswered = selected
                .stream()
                .filter(s -> !covered.contains(s.getId()))
                .count();
            assertEquals(session < 7 ? 10L : 5L, unanswered);
            for (Scenario scenario : selected) {
                covered.add(scenario.getId());
                history.add(attempt(scenario, false, NEWER));
            }
        }
        assertEquals(75, covered.size());
    }

    @Test
    void unansweredComesBeforeErrorsThenFrequentlyCorrect() {
        var scenarios = bank(3);
        List<StageAttempt> history = new ArrayList<>();
        for (int i = 0; i < 5; i++) history.add(attempt(scenarios.get(0), false, NEWER));
        for (int i = 0; i < 10; i++) history.add(attempt(scenarios.get(2), true, OLDER));
        assertEquals(List.of(scenarios.get(1), scenarios.get(0), scenarios.get(2)), curator.select(scenarios, history, 3, new Random(1)));
    }

    @Test
    void totalsAllAttemptsAndUsesLatestAnswerRegardlessOfInputOrder() {
        var scenarios = bank(2);
        var history = List.of(
            attempt(scenarios.get(0), false, NEWER),
            attempt(scenarios.get(0), true, OLDER),
            attempt(scenarios.get(1), false, OLDER),
            attempt(scenarios.get(1), true, OLDER)
        );
        assertEquals(List.of(scenarios.get(1), scenarios.get(0)), curator.select(scenarios, history, 2, new Random(1)));
    }

    @Test
    void returnsOriginalScenariosAndDoesNotChangeInputs() {
        var scenarios = bank(10);
        var original = List.copyOf(scenarios);
        List<StageAttempt> history = new ArrayList<>(List.of(attempt(scenarios.get(0), false, NEWER)));
        var savedHistory = List.copyOf(history);
        for (Scenario selected : curator.select(scenarios, history)) {
            assertSame(scenarios.get(selected.getId().intValue() - 1), selected);
        }
        assertEquals(original, scenarios);
        assertEquals(savedHistory, history);
        assertFalse(history.get(0).getCorrect());
    }

    @Test
    void eachCallUsesOnlyItsSuppliedHistory() {
        var scenarios = bank(2);
        assertEquals(
            List.of(scenarios.get(1)),
            curator.select(scenarios, List.of(attempt(scenarios.get(0), false, NEWER)), 1, new Random(1))
        );
        assertEquals(
            List.of(scenarios.get(0)),
            curator.select(scenarios, List.of(attempt(scenarios.get(1), false, NEWER)), 1, new Random(1))
        );
    }

    @Test
    void exactTiesCanBeReproducedWithASeed() {
        var scenarios = bank(75);
        var first = curator.select(scenarios, List.of(), 10, new Random(1));
        assertEquals(first, curator.select(scenarios, List.of(), 10, new Random(1)));
        assertNotEquals(first, curator.select(scenarios, List.of(), 10, new Random(2)));
    }

    @Test
    void rejectsDuplicateScenariosOrStagesAndInsufficientBanks() {
        var scenarios = bank(2);
        assertThrows(IllegalArgumentException.class, () ->
            curator.select(List.of(scenarios.get(0), scenarios.get(0)), List.of(), 1, new Random(1))
        );
        scenarios.get(1).setStartingStage(scenarios.get(0).getStartingStage());
        assertThrows(IllegalArgumentException.class, () -> curator.select(scenarios, List.of(), 1, new Random(1)));
        assertThrows(IllegalArgumentException.class, () -> curator.select(bank(9), List.of()));
        assertThrows(IllegalArgumentException.class, () -> curator.select(bank(10), List.of(), 0, new Random(1)));
    }
}
