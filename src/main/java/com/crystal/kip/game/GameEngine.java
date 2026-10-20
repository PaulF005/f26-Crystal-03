package com.crystal.kip.game;

import com.crystal.kip.domain.Answer;
import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Stage;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.domain.UserProfile;
import com.crystal.kip.repository.AnswerRepository;
import org.springframework.stereotype.Service;

@Service
public class GameEngine {

    private final ScenarioSelector scenarioSelector;
    private final StageSelector stageSelector;
    private final AnswerRepository answerRepository;

    public GameEngine(ScenarioSelector scenarioSelector, StageSelector stageSelector, AnswerRepository answerRepository) {
        this.scenarioSelector = scenarioSelector;
        this.stageSelector = stageSelector;
        this.answerRepository = answerRepository;
    }

    public GameActive startActive(UserProfile user, Game game, Topic topic) {
        GameActive active = new GameActive(user, game, topic);

        // Invoke the new GameActive to start the very first Scenario
        advanceScenario(active);

        return active;
    }

    private void endSession(GameActive active) {
        // Clean up the active session
    }

    public void completeSession(GameActive active) {
        // Commit results to db
        endSession(active);
    }

    public void abandonSession(GameActive active) {
        // Discard results
        endSession(active);
    }

    public void advanceScenario(GameActive active) {
        Scenario scenario = scenarioSelector.selectNew(active);

        // This "should not" happen
        if (scenario == null) throw new IllegalStateException("Scenario was not successfully selected by ScenarioSelector");

        // If a new Scenario was successfully chosen, invoke the GameActive to start it
        active.startScenario(scenario);
    }

    public void completeScenario(GameActive active) {
        advanceScenario(active);
    }

    public void selectAnswer(GameActive active, Long answerId) {
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

        // If the selected Answer terminates the current Scenario, complete the Scenario
        if (stageSelector.isTerminal(selectedAnswer)) {
            completeScenario(active);
            return;
        }

        // Otherwise, determine the next Stage from the selected Answer
        Stage nextStage = stageSelector.selectNext(selectedAnswer);

        // This "should not" happen:
        // every Answer should either point to another Stage or terminate the Scenario
        if (nextStage == null) throw new IllegalStateException("Answer has neither a next stage nor a terminal resolution");

        // If a further Stage exists, invoke the GameActive to start it
        active.startStage(nextStage);
    }
}
