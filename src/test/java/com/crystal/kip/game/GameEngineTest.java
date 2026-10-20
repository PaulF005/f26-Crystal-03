package com.crystal.kip.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.crystal.kip.domain.Answer;
import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.Question;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Stage;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.domain.UserProfile;
import com.crystal.kip.repository.AnswerRepository;
import com.crystal.kip.repository.LinkRepo.LinkScenarioRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class GameEngineTest {

    private GameEngine gameEngine;
    private ScenarioSelector scenarioSelector;
    private StageSelector stageSelector;

    private LinkScenarioRepository linkScenarioRepository;
    private AnswerRepository answerRepository;

    private GameActive active;

    private UserProfile user;
    private Game game;
    private Topic topic;

    @BeforeEach
    void setUp() {
        linkScenarioRepository = mock(LinkScenarioRepository.class);
        answerRepository = mock(AnswerRepository.class);

        scenarioSelector = new ScenarioSelector(linkScenarioRepository);
        stageSelector = new StageSelector();

        gameEngine = new GameEngine(scenarioSelector, stageSelector, answerRepository);

        user = new UserProfile();
        game = new Game().id(1L);
        topic = new Topic().id(1L);

        Scenario startingScenario = new Scenario().id(1L).name("Starting Scenario").game(game).topic(topic);

        when(linkScenarioRepository.findByGameIdAndTopicId(game.getId(), topic.getId())).thenReturn(List.of(startingScenario));

        active = gameEngine.startActive(user, game, topic);
    }

    @Test
    void testStartActive() {
        assertNotNull(active);
        assertEquals(game, active.getGame());
        assertEquals(topic, active.getTopic());
        assertNotNull(active.getScenarioCurrent());
    }

    @Test
    @Disabled("Not yet implemented")
    void testEndActive() {}

    @Test
    @Disabled("Not yet implemented")
    void testCompleteActive() {}

    @Test
    @Disabled("Not yet implemented")
    void testAbandonActive() {}

    @Test
    void testAdvanceScenario() {
        Scenario scenario = new Scenario().id(2L).name("Test Scenario").game(game).topic(topic);

        when(linkScenarioRepository.findByGameIdAndTopicId(game.getId(), topic.getId())).thenReturn(List.of(scenario));

        gameEngine.advanceScenario(active);

        assertEquals(scenario, active.getScenarioCurrent());
    }

    @Test
    @Disabled("Not yet implemented")
    void testCompleteScenario() {}

    @Test
    void testSelectAnswerAdvancesStage() {
        Question question = new Question().id(1L).question("Test Question");

        Stage currentStage = new Stage().id(1L).question(question);

        Stage nextStage = new Stage().id(2L);

        Answer answer = new Answer().id(1L).question(question).nextStage(nextStage);

        active.startStage(currentStage);

        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));

        gameEngine.selectAnswer(active, answer.getId());

        assertEquals(nextStage, active.getStageCurrent());
    }

    @Test
    void testSelectAnswerRejectsAnswerFromDifferentQuestion() {
        Question currentQuestion = new Question().id(1L).question("Current Question");

        Question differentQuestion = new Question().id(2L).question("Different Question");

        Stage currentStage = new Stage().id(1L).question(currentQuestion);

        Answer answer = new Answer().id(1L).question(differentQuestion);

        active.startStage(currentStage);

        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));

        assertThrows(IllegalArgumentException.class, () -> gameEngine.selectAnswer(active, answer.getId()));
    }

    @Test
    void testSelectAnswerNotFound() {
        Long answerId = 999L;

        when(answerRepository.findById(answerId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> gameEngine.selectAnswer(active, answerId));
    }

    @Test
    @Disabled("Add once terminal scenario behavior is finalized")
    void testSelectTerminalAnswer() {}
}
