package com.crystal.kip.progress_update;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.crystal.kip.domain.Answer;
import com.crystal.kip.domain.Concept;
import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.Question;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Stage;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.domain.UserProfile;
import com.crystal.kip.game.GameActive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProgressUpdaterTest {

    @Mock
    private ProgressUpdaterConcept progressUpdaterConcept;

    @Mock
    private ProgressUpdaterTopic progressUpdaterTopic;

    @Mock
    private ProgressUpdaterGame progressUpdaterGame;

    private ProgressUpdater progressUpdater;

    private UserProfile user;
    private Game game;
    private Topic topic;
    private Scenario scenario;

    @BeforeEach
    void setUp() {
        progressUpdater = new ProgressUpdater(progressUpdaterConcept, progressUpdaterTopic, progressUpdaterGame);

        user = new UserProfile().id(1L);
        game = new Game().id(10L).name("Test Game");
        topic = new Topic().id(20L).name("Test Topic");
        scenario = new Scenario().id(30L).game(game).topic(topic);
    }

    @Test
    void updateProgressUpdatesSpecificConceptsTopicAndGame() {
        Concept conceptOne = new Concept().id(101L).name("Concept One").topic(topic);
        Concept conceptTwo = new Concept().id(102L).name("Concept Two").topic(topic);

        GameActive active = new GameActive(user, game, topic);
        active.startScenario(scenario);

        recordResult(active, 201L, conceptOne, true);
        recordResult(active, 202L, conceptOne, false);
        recordResult(active, 203L, conceptTwo, true);

        when(progressUpdaterConcept.updateConceptProgress(conceptOne.getId(), 1)).thenReturn(0.20f);
        when(progressUpdaterConcept.updateConceptProgress(conceptTwo.getId(), 1)).thenReturn(0.10f);

        progressUpdater.updateProgress(active);

        verify(progressUpdaterConcept).updateConceptProgress(conceptOne.getId(), 1);
        verify(progressUpdaterConcept).updateConceptProgress(conceptTwo.getId(), 1);
        verify(progressUpdaterTopic).updateTopicProgress(topic.getId(), 0.30f);
        verify(progressUpdaterGame).updateGameProgress(game.getId(), 2.0f / 3.0f);
    }

    @Test
    void updateProgressRejectsSessionWithNoStageResults() {
        GameActive active = new GameActive(user, game, topic);

        assertThrows(IllegalStateException.class, () -> progressUpdater.updateProgress(active));

        verifyNoInteractions(progressUpdaterConcept, progressUpdaterTopic, progressUpdaterGame);
    }

    @Test
    void updateProgressRejectsQuestionWithoutConcept() {
        GameActive active = new GameActive(user, game, topic);
        active.startScenario(scenario);

        Question question = new Question().id(301L).question("Question without concept");

        Stage stage = new Stage().id(401L).scenario(scenario).question(question);

        scenario.addStage(stage);
        active.startStage(stage);

        Answer answer = new Answer().id(501L).question(question).correct(true);

        active.recordStageResult(answer);

        assertThrows(IllegalStateException.class, () -> progressUpdater.updateProgress(active));

        verifyNoInteractions(progressUpdaterConcept, progressUpdaterTopic, progressUpdaterGame);
    }

    private void recordResult(GameActive active, Long id, Concept concept, boolean correct) {
        Question question = new Question()
            .id(id)
            .question("Question " + id)
            .concept(concept);

        Stage stage = new Stage().id(id).scenario(scenario).question(question);

        scenario.addStage(stage);
        active.startStage(stage);

        Answer answer = new Answer().id(id).question(question).correct(correct);

        active.recordStageResult(answer);
    }
}
