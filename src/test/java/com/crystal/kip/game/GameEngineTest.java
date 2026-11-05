package com.crystal.kip.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.crystal.kip.domain.Answer;
import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.GameSession;
import com.crystal.kip.domain.Question;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Stage;
import com.crystal.kip.domain.StageAttempt;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.domain.UserProfile;
import com.crystal.kip.domain.enumeration.ScenarioResolution;
import com.crystal.kip.progress_update.ProgressUpdater;
import com.crystal.kip.repository.AnswerRepository;
import com.crystal.kip.repository.GameSessionRepository;
import com.crystal.kip.repository.StageAttemptRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GameEngineTest {

    @Mock
    private ScenarioSelector scenarioSelector;

    @Mock
    private StageSelector stageSelector;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private GameSessionRepository gameSessionRepository;

    @Mock
    private StageAttemptRepository stageAttemptRepository;

    @Mock
    private ProgressUpdater progressUpdater;

    @Spy
    private SessionManager sessionManager = new SessionManager();

    @InjectMocks
    private GameEngine gameEngine;

    private UserProfile user;
    private Game game;
    private Topic topic;

    @BeforeEach
    void setUp() {
        user = new UserProfile().id(1L);
        game = new Game().id(1L).name("Test Game");
        topic = new Topic().id(1L).name("Test Topic");
    }

    @Test
    void testStartActiveStartsFirstScenarioAndStageAndRegistersSession() {
        Scenario scenario = scenario(1L);
        Question question = new Question().id(1L).question("Question");
        Stage startingStage = stage(1L, scenario, question);
        scenario.setStartingStage(startingStage);

        when(scenarioSelector.selectNew(any(GameActive.class))).thenReturn(scenario);
        when(stageSelector.selectFirst(scenario)).thenReturn(startingStage);

        GameActive active = gameEngine.startActive(user, game, topic);

        assertNotNull(active);
        assertEquals(user, active.getUser());
        assertEquals(game, active.getGame());
        assertEquals(topic, active.getTopic());
        assertEquals(scenario, active.getScenarioCurrent());
        assertEquals(startingStage, active.getStageCurrent());
        assertTrue(sessionManager.contains(active.getId()));
    }

    @Test
    void testStartActiveCleansUpSessionIfScenarioCannotBeSelected() {
        when(scenarioSelector.selectNew(any(GameActive.class))).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> gameEngine.startActive(user, game, topic));

        verify(sessionManager).remove(any(UUID.class));
    }

    @Test
    void testStartActiveCleansUpSessionIfScenarioHasNoStartingStage() {
        Scenario scenario = scenario(1L);

        when(scenarioSelector.selectNew(any(GameActive.class))).thenReturn(scenario);
        when(stageSelector.selectFirst(scenario)).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> gameEngine.startActive(user, game, topic));

        verify(sessionManager).remove(any(UUID.class));
    }

    @Test
    void testSelectAnswerRecordsResultAndAdvancesStage() {
        Scenario scenario = scenario(1L);
        Question question = new Question().id(1L).question("Question");
        Stage currentStage = stage(1L, scenario, question);
        Stage nextStage = stage(2L, scenario, new Question().id(2L).question("Next"));
        scenario.setStartingStage(currentStage);

        GameActive active = startActive(scenario, currentStage);

        Answer answer = new Answer().id(1L).text("Answer").correct(true).question(question).nextStage(nextStage);

        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));
        when(stageSelector.isTerminal(answer)).thenReturn(false);
        when(stageSelector.selectNext(answer)).thenReturn(nextStage);

        gameEngine.selectAnswer(active, answer.getId());

        assertEquals(nextStage, active.getStageCurrent());
        assertEquals(1, active.getStageResults().size());
        assertEquals(currentStage, active.getStageResults().get(0).getStage());
        assertEquals(answer, active.getStageResults().get(0).getSelectedAnswer());
        assertTrue(active.getStageResults().get(0).isCorrect());
    }

    @Test
    void testSelectAnswerRejectsAnswerFromDifferentQuestionWithoutRecordingResult() {
        Scenario scenario = scenario(1L);
        Question currentQuestion = new Question().id(1L).question("Current");
        Question differentQuestion = new Question().id(2L).question("Different");
        Stage currentStage = stage(1L, scenario, currentQuestion);
        scenario.setStartingStage(currentStage);

        GameActive active = startActive(scenario, currentStage);

        Answer answer = new Answer().id(1L).question(differentQuestion).correct(true);

        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));

        assertThrows(IllegalArgumentException.class, () -> gameEngine.selectAnswer(active, answer.getId()));

        assertTrue(active.getStageResults().isEmpty());
        assertEquals(currentStage, active.getStageCurrent());
    }

    @Test
    void testSelectAnswerRejectsMissingAnswer() {
        Scenario scenario = scenario(1L);
        Question question = new Question().id(1L);
        Stage currentStage = stage(1L, scenario, question);
        scenario.setStartingStage(currentStage);

        GameActive active = startActive(scenario, currentStage);

        when(answerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> gameEngine.selectAnswer(active, 999L));

        assertTrue(active.getStageResults().isEmpty());
    }

    @Test
    void testSelectTerminalAnswerCompletesScenarioButDoesNotAutomaticallyStartAnother() {
        Scenario scenario = scenario(1L);
        Question question = new Question().id(1L).question("Terminal question");
        Stage currentStage = stage(1L, scenario, question);
        scenario.setStartingStage(currentStage);

        GameActive active = startActive(scenario, currentStage);

        Answer answer = new Answer().id(1L).question(question).correct(true).terminalResolution(ScenarioResolution.POSITIVE);

        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));
        when(stageSelector.isTerminal(answer)).thenReturn(true);

        gameEngine.selectAnswer(active, answer.getId());

        assertEquals(1, active.getStageResults().size());
        assertEquals(1, active.getScenarioResults().size());
        assertEquals(ScenarioResolution.POSITIVE, active.getScenarioResults().get(0).getResolution());
        assertNull(active.getScenarioCurrent());
        assertNull(active.getStageCurrent());
        assertTrue(sessionManager.contains(active.getId()));

        verify(scenarioSelector, never()).selectNew(active);
    }

    @Test
    void testContinueSessionStartsNextScenarioAfterResultScreen() {
        Scenario firstScenario = scenario(1L);
        Question firstQuestion = new Question().id(1L);
        Stage firstStage = stage(1L, firstScenario, firstQuestion);
        firstScenario.setStartingStage(firstStage);

        GameActive active = startActive(firstScenario, firstStage);
        gameEngine.completeScenario(active, ScenarioResolution.POSITIVE);

        Scenario secondScenario = scenario(2L);
        Stage secondStage = stage(2L, secondScenario, new Question().id(2L));
        secondScenario.setStartingStage(secondStage);

        when(scenarioSelector.selectNew(active)).thenReturn(secondScenario);
        when(stageSelector.selectFirst(secondScenario)).thenReturn(secondStage);

        gameEngine.continueSession(active);

        assertEquals(secondScenario, active.getScenarioCurrent());
        assertEquals(secondStage, active.getStageCurrent());
        assertTrue(sessionManager.contains(active.getId()));
    }

    @Test
    void testContinueSessionRejectedWhileScenarioStillActive() {
        Scenario scenario = scenario(1L);
        Stage stage = stage(1L, scenario, new Question().id(1L));
        scenario.setStartingStage(stage);

        GameActive active = startActive(scenario, stage);

        assertThrows(IllegalStateException.class, () -> gameEngine.continueSession(active));

        verifyNoInteractions(gameSessionRepository, stageAttemptRepository, progressUpdater);
    }

    @Test
    void testContinueSessionWithNoRemainingScenarioPersistsAndEndsSession() {
        Scenario scenario = scenario(1L);
        Question question = new Question().id(1L);
        Stage stage = stage(1L, scenario, question);
        scenario.setStartingStage(stage);

        GameActive active = startActive(scenario, stage);

        Answer answer = new Answer().id(1L).question(question).correct(true).terminalResolution(ScenarioResolution.POSITIVE);

        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));
        when(stageSelector.isTerminal(answer)).thenReturn(true);
        gameEngine.selectAnswer(active, answer.getId());

        when(scenarioSelector.selectNew(active)).thenReturn(null);
        when(gameSessionRepository.save(any(GameSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        gameEngine.continueSession(active);

        assertFalse(sessionManager.contains(active.getId()));

        ArgumentCaptor<GameSession> sessionCaptor = ArgumentCaptor.forClass(GameSession.class);
        verify(gameSessionRepository).save(sessionCaptor.capture());

        GameSession savedSession = sessionCaptor.getValue();
        assertEquals(user, savedSession.getUser());
        assertEquals(game, savedSession.getGame());
        assertEquals(active.getStartedAt(), savedSession.getStartedAt());
        assertNotNull(savedSession.getCompletedAt());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<StageAttempt>> attemptsCaptor = ArgumentCaptor.forClass(List.class);
        verify(stageAttemptRepository).saveAll(attemptsCaptor.capture());

        List<StageAttempt> attempts = new ArrayList<>(attemptsCaptor.getValue());

        assertEquals(1, attempts.size());
        StageAttempt attempt = attempts.get(0);
        assertEquals(user, attempt.getUser());
        assertEquals(stage, attempt.getStage());
        assertEquals(answer, attempt.getSelectedAnswer());
        assertEquals(savedSession, attempt.getGameSession());
        assertEquals(Boolean.TRUE, attempt.getCorrect());
        assertNotNull(attempt.getAnsweredAt());

        verify(progressUpdater).updateProgress(active);
    }

    @Test
    void testAbandonSessionRemovesActiveSessionWithoutPersistenceOrProgress() {
        Scenario scenario = scenario(1L);
        Stage stage = stage(1L, scenario, new Question().id(1L));
        scenario.setStartingStage(stage);

        GameActive active = startActive(scenario, stage);

        gameEngine.abandonSession(active);

        assertFalse(sessionManager.contains(active.getId()));
        verifyNoInteractions(gameSessionRepository, stageAttemptRepository, progressUpdater);
    }

    @Test
    void testOperationsRejectSessionThatIsNoLongerActive() {
        GameActive inactive = new GameActive(user, game, topic);

        assertThrows(IllegalStateException.class, () -> gameEngine.abandonSession(inactive));
    }

    private GameActive startActive(Scenario scenario, Stage startingStage) {
        when(scenarioSelector.selectNew(any(GameActive.class))).thenReturn(scenario);
        when(stageSelector.selectFirst(scenario)).thenReturn(startingStage);

        GameActive active = gameEngine.startActive(user, game, topic);

        // Prevent the broad start-session stub from affecting later scenario-selection calls.
        org.mockito.Mockito.reset(scenarioSelector);

        return active;
    }

    private Scenario scenario(Long id) {
        return new Scenario()
            .id(id)
            .name("Scenario " + id)
            .game(game)
            .topic(topic);
    }

    private Stage stage(Long id, Scenario scenario, Question question) {
        Stage stage = new Stage().id(id).scenario(scenario).question(question);

        scenario.addStage(stage);
        return stage;
    }
}
