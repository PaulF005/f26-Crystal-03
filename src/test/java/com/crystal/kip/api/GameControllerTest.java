package com.crystal.kip.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.crystal.kip.api.dto.AnswerResultDTO;
import com.crystal.kip.api.dto.ContinueDTO;
import com.crystal.kip.api.dto.RequestStartDTO;
import com.crystal.kip.api.dto.SessionDTO;
import com.crystal.kip.domain.Answer;
import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.Question;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Stage;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.domain.UserProfile;
import com.crystal.kip.domain.enumeration.ScenarioResolution;
import com.crystal.kip.game.GameActive;
import com.crystal.kip.game.GameEngine;
import com.crystal.kip.game.SessionManager;
import com.crystal.kip.repository.GameRepository;
import com.crystal.kip.repository.LinkRepo.LinkUserProfileRepository;
import com.crystal.kip.repository.TopicRepository;
import com.crystal.kip.security.SecurityUtils;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class GameControllerTest {

    private static final String USERNAME = "testUser";

    private GameEngine gameEngine;
    private SessionManager sessionManager;
    private GameRepository gameRepository;
    private TopicRepository topicRepository;
    private LinkUserProfileRepository userProfileRepository;
    private GameController controller;
    private MockedStatic<SecurityUtils> securityUtils;

    private UserProfile user;
    private Game game;
    private Topic topic;

    @BeforeEach
    void setUp() {
        gameEngine = mock(GameEngine.class);
        sessionManager = new SessionManager();
        gameRepository = mock(GameRepository.class);
        topicRepository = mock(TopicRepository.class);
        userProfileRepository = mock(LinkUserProfileRepository.class);

        controller = new GameController(gameEngine, sessionManager, gameRepository, topicRepository, userProfileRepository);

        user = new UserProfile().id(1L).username(USERNAME);
        game = new Game().id(10L).name("Fact or Fiction");
        topic = new Topic().id(20L).name("Test Topic");

        securityUtils = mockStatic(SecurityUtils.class);
        securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(USERNAME));
        when(userProfileRepository.findOneByDataUserLogin(USERNAME)).thenReturn(Optional.of(user));
    }

    @AfterEach
    void tearDown() {
        if (securityUtils != null) {
            securityUtils.close();
        }
    }

    @Test
    void testStartGameReturnsRenderableSessionState() {
        Scenario scenario = new Scenario().id(100L).game(game).topic(topic);
        Question question = new Question().id(200L).question("Is this true?");
        Stage stage = new Stage().id(300L).scenario(scenario).question(question);
        Answer answer = new Answer().id(400L).text("Yes").question(question);

        question.addAnswer(answer);
        scenario.addStage(stage);
        scenario.setStartingStage(stage);

        GameActive active = new GameActive(user, game, topic);
        active.startScenario(scenario);
        active.startStage(stage);

        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
        when(topicRepository.findById(topic.getId())).thenReturn(Optional.of(topic));
        when(gameEngine.startActive(user, game, topic)).thenReturn(active);

        SessionDTO response = controller.startGame(new RequestStartDTO(game.getId(), topic.getId()));

        assertEquals(active.getId(), response.id());
        assertEquals(game.getName(), response.gameTitle());
        assertEquals(scenario.getId(), response.currentScenario().scenarioId());
        assertEquals(1, response.currentScenario().stageNumber());
        assertEquals(1, response.currentScenario().stageCount());
        assertEquals(stage.getId(), response.currentScenario().currentStage().stageId());
        assertEquals(question.getQuestion(), response.currentScenario().currentStage().prompt());
        assertEquals(1, response.currentScenario().currentStage().answers().size());
        assertEquals(answer.getId(), response.currentScenario().currentStage().answers().get(0).answerId());
    }

    @Test
    void testStartGameRejectsMissingIds() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            controller.startGame(new RequestStartDTO(null, topic.getId()))
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(gameEngine, never()).startActive(any(), any(), any());
    }

    @Test
    void testStartGameReturns404ForMissingGame() {
        when(gameRepository.findById(game.getId())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            controller.startGame(new RequestStartDTO(game.getId(), topic.getId()))
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testSelectAnswerReturnsNextStageForNonTerminalAnswer() {
        Scenario scenario = new Scenario().id(100L).game(game).topic(topic);
        Question firstQuestion = new Question().id(200L).question("First");
        Question secondQuestion = new Question().id(201L).question("Second");
        Stage firstStage = new Stage().id(300L).scenario(scenario).question(firstQuestion);
        Stage secondStage = new Stage().id(301L).scenario(scenario).question(secondQuestion);
        Answer nextAnswer = new Answer().id(401L).text("Continue").question(secondQuestion);
        secondQuestion.addAnswer(nextAnswer);
        scenario.addStage(firstStage);
        scenario.addStage(secondStage);

        GameActive active = activeSession(scenario, firstStage);

        doAnswer(invocation -> {
            active.startStage(secondStage);
            return null;
        })
            .when(gameEngine)
            .selectAnswer(active, 400L);

        AnswerResultDTO response = controller.selectAnswer(active.getId(), 400L);

        assertNull(response.result());
        assertEquals(secondStage.getId(), response.nextStage().stageId());
        assertEquals(secondQuestion.getQuestion(), response.nextStage().prompt());
    }

    @Test
    void testSelectTerminalAnswerReturnsScenarioResult() {
        Scenario scenario = new Scenario().id(100L).game(game).topic(topic);
        Question question = new Question().id(200L).question("Terminal");
        Stage stage = new Stage().id(300L).scenario(scenario).question(question);
        Answer terminalAnswer = new Answer()
            .id(400L)
            .text("Finish")
            .outcomeText("The scenario ends here.")
            .correct(true)
            .question(question)
            .terminalResolution(ScenarioResolution.POSITIVE);

        GameActive active = activeSession(scenario, stage);

        doAnswer(invocation -> {
            active.recordStageResult(terminalAnswer);
            active.recordScenarioResult(ScenarioResolution.POSITIVE);
            return null;
        })
            .when(gameEngine)
            .selectAnswer(active, terminalAnswer.getId());

        AnswerResultDTO response = controller.selectAnswer(active.getId(), terminalAnswer.getId());

        assertNull(response.nextStage());
        assertNull(response.result().feedbackText());
        assertEquals(terminalAnswer.getOutcomeText(), response.result().outcomeText());
    }

    @Test
    void testContinueReturnsNextScenarioWhenSessionRemainsActive() {
        Scenario firstScenario = new Scenario().id(100L).game(game).topic(topic);
        Stage firstStage = new Stage().id(300L).scenario(firstScenario).question(new Question().id(200L));
        GameActive active = activeSession(firstScenario, firstStage);
        active.recordScenarioResult(ScenarioResolution.POSITIVE);

        Scenario secondScenario = new Scenario().id(101L).game(game).topic(topic);
        Question secondQuestion = new Question().id(201L).question("Next scenario");
        Stage secondStage = new Stage().id(301L).scenario(secondScenario).question(secondQuestion);
        secondScenario.addStage(secondStage);

        doAnswer(invocation -> {
            active.startScenario(secondScenario);
            active.startStage(secondStage);
            return null;
        })
            .when(gameEngine)
            .continueSession(active);

        ContinueDTO response = controller.continueGame(active.getId());

        assertFalse(response.complete());
        assertEquals(active.getId(), response.session().id());
        assertEquals(secondScenario.getId(), response.session().currentScenario().scenarioId());
    }

    @Test
    void testContinueReturnsCompleteWhenEngineEndsSession() {
        Scenario scenario = new Scenario().id(100L).game(game).topic(topic);
        Stage stage = new Stage().id(300L).scenario(scenario).question(new Question().id(200L));
        GameActive active = activeSession(scenario, stage);
        active.recordScenarioResult(ScenarioResolution.POSITIVE);

        doAnswer(invocation -> {
            sessionManager.remove(active.getId());
            return null;
        })
            .when(gameEngine)
            .continueSession(active);

        ContinueDTO response = controller.continueGame(active.getId());

        assertTrue(response.complete());
        assertNull(response.session());
    }

    @Test
    void testAbandonDelegatesToEngineForOwnedSession() {
        Scenario scenario = new Scenario().id(100L).game(game).topic(topic);
        Stage stage = new Stage().id(300L).scenario(scenario).question(new Question().id(200L));
        GameActive active = activeSession(scenario, stage);

        controller.abandonGame(active.getId());

        verify(gameEngine).abandonSession(active);
    }

    @Test
    void testSessionAccessRejectedForDifferentUser() {
        Scenario scenario = new Scenario().id(100L).game(game).topic(topic);
        Stage stage = new Stage().id(300L).scenario(scenario).question(new Question().id(200L));
        GameActive active = activeSession(scenario, stage);

        UserProfile otherUser = new UserProfile().id(2L).username("other");
        when(userProfileRepository.findOneByDataUserLogin(USERNAME)).thenReturn(Optional.of(otherUser));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.abandonGame(active.getId()));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(gameEngine, never()).abandonSession(active);
    }

    @Test
    void testUnknownSessionReturns404() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            controller.abandonGame(java.util.UUID.randomUUID())
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testCurrentUserProfileMustExist() {
        when(userProfileRepository.findOneByDataUserLogin(USERNAME)).thenReturn(Optional.empty());
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
        when(topicRepository.findById(topic.getId())).thenReturn(Optional.of(topic));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            controller.startGame(new RequestStartDTO(game.getId(), topic.getId()))
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    private GameActive activeSession(Scenario scenario, Stage stage) {
        scenario.addStage(stage);

        GameActive active = new GameActive(user, game, topic);
        active.startScenario(scenario);
        active.startStage(stage);
        sessionManager.add(active);
        return active;
    }
}
