package com.crystal.kip.game;

import com.crystal.kip.domain.Answer;
import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.GameSession;
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
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GameEngine {

    private final ScenarioSelector scenarioSelector;
    private final StageSelector stageSelector;
    private final AnswerRepository answerRepository;
    private final GameSessionRepository gameSessionRepository;
    private final StageAttemptRepository stageAttemptRepository;
    private final SessionManager sessionManager;
    private final ProgressUpdater progressUpdater;

    public GameEngine(
        ScenarioSelector scenarioSelector,
        StageSelector stageSelector,
        AnswerRepository answerRepository,
        GameSessionRepository gameSessionRepository,
        StageAttemptRepository stageAttemptRepository,
        SessionManager sessionManager,
        ProgressUpdater progressUpdater
    ) {
        this.scenarioSelector = scenarioSelector;
        this.stageSelector = stageSelector;
        this.answerRepository = answerRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.stageAttemptRepository = stageAttemptRepository;
        this.sessionManager = sessionManager;
        this.progressUpdater = progressUpdater;
    }

    public GameActive startActive(UserProfile user, Game game, Topic topic) {
        GameActive active = new GameActive(user, game, topic);

        sessionManager.add(active);

        try {
            // Invoke the new GameActive to start the very first Scenario
            advanceScenario(active);
        } catch (RuntimeException e) {
            sessionManager.remove(active.getId());
            throw e;
        }

        return active;
    }

    private void endSession(GameActive active) {
        sessionManager.remove(active.getId());
    }

    @Transactional
    public void completeSession(GameActive active) {
        requireActive(active);

        GameSession gameSession = new GameSession()
            .startedAt(active.getStartedAt())
            .completedAt(Instant.now())
            .user(active.getUser())
            .game(active.getGame());

        GameSession savedGameSession = gameSessionRepository.save(gameSession);

        List<StageAttempt> attempts = active
            .getStageResults()
            .stream()
            .map(result ->
                new StageAttempt()
                    .answeredAt(result.getAnsweredAt())
                    .correct(result.isCorrect())
                    .user(active.getUser())
                    .stage(result.getStage())
                    .selectedAnswer(result.getSelectedAnswer())
                    .gameSession(savedGameSession)
            )
            .toList();

        stageAttemptRepository.saveAll(attempts);

        // Update the user's Concept, Topic, and Game progress
        // from the completed Session's results
        progressUpdater.updateProgress(active);

        endSession(active);
    }

    public void abandonSession(GameActive active) {
        requireActive(active);

        // Abandoned sessions are discarded rather than persisted
        endSession(active);
    }

    @Transactional
    public void continueSession(GameActive active) {
        requireActive(active);

        if (active.getScenarioCurrent() != null) throw new IllegalStateException(
            "Cannot continue session while a Scenario is still active"
        );

        Scenario scenario = scenarioSelector.selectNew(active);

        // No remaining Scenario means the session is complete
        if (scenario == null) {
            completeSession(active);
            return;
        }

        startScenario(active, scenario);
    }

    public void advanceScenario(GameActive active) {
        requireActive(active);

        Scenario scenario = scenarioSelector.selectNew(active);

        // This "should not" happen when initially starting a session
        if (scenario == null) throw new IllegalStateException("Scenario was not successfully selected by ScenarioSelector");

        startScenario(active, scenario);
    }

    private void startScenario(GameActive active, Scenario scenario) {
        active.startScenario(scenario);

        Stage startingStage = stageSelector.selectFirst(scenario);

        // This "should not" happen. Every Scenario should have a starting Stage
        if (startingStage == null) throw new IllegalStateException("Scenario does not have a starting Stage");

        active.startStage(startingStage);
    }

    public void completeScenario(GameActive active, ScenarioResolution resolution) {
        requireActive(active);

        active.recordScenarioResult(resolution);

        // TODO: Control flow to enable end-of-Scenario screen (feedback, etc.)
        // and don't advance Scenario immediately
    }

    public void selectAnswer(GameActive active, Long answerId) {
        requireActive(active);

        if (active.getStageCurrent() == null) throw new IllegalStateException("There is no active Stage awaiting an Answer");

        // Retrieve the Answer represented by the button selected by the user
        Answer selectedAnswer = answerRepository
            .findById(answerId)
            .orElseThrow(() -> new IllegalArgumentException("Answer not found: " + answerId));

        submitAnswer(active, selectedAnswer);
    }

    private void submitAnswer(GameActive active, Answer selectedAnswer) {
        Stage currentStage = active.getStageCurrent();

        // Ensure the selected Answer actually belongs to the current Stage's Question
        if (!selectedAnswer.getQuestion().equals(currentStage.getQuestion())) throw new IllegalArgumentException(
            "Selected answer does not belong to the current question"
        );

        // Remember this Stage result so it can be persisted if the Session is completed
        active.recordStageResult(selectedAnswer);

        // If the selected Answer terminates the current Scenario, complete the Scenario
        if (stageSelector.isTerminal(selectedAnswer)) {
            completeScenario(active, selectedAnswer.getTerminalResolution());
            return;
        }

        // Otherwise, determine the next Stage from the selected Answer
        Stage nextStage = stageSelector.selectNext(selectedAnswer);

        // This "should not" happen:
        // every Answer should either point to another Stage or terminate the Scenario
        if (nextStage == null) throw new IllegalStateException("Answer has neither a next stage nor a terminal resolution");

        active.startStage(nextStage);
    }

    private void requireActive(GameActive active) {
        if (active == null || sessionManager.get(active.getId()) != active) throw new IllegalStateException(
            "Game session is no longer active"
        );
    }
}
