package com.crystal.kip.feedback_game;

import com.crystal.kip.progress_update.ProgressUpdater;
import com.crystal.kip.progress_update.ProgressUpdaterConcept;

/**
 * Used to return question if it is correct or wrong and an explanation.
 * Also keeps track of number of questions ansered and correct to send to progress tracker
 * Feedback
 */
public class Feedback {

    private final ProgressUpdater progressUpdater;
    private int numberOfQuestions;
    private int numberOfAnswed;
    private int numberOfRight;

    /**
     *
     * @param numberOfQuestions Number of questions for given scenario
     * @param progressUpdaterTopic
     */
    public Feedback(int numberOfQuestions, ProgressUpdater progressUpdater) {
        this.numberOfQuestions = numberOfQuestions;
        this.numberOfAnswed = 0;
        this.numberOfRight = 0;
        this.progressUpdater = progressUpdater;
    }

    /**
     * To be used in a game session to see if user's choice was correct, return with a correct or wrong with correct choice, and auto send info
     */
    public String questionDecider(int choice, int answer, String explanation) {
        numberOfAnswed++;
        String questionResponse;
        if (choice == answer) {
            questionResponse = questionRight(explanation);
            checkForMax();
            return questionResponse;
        } else {
            questionResponse = questionWrong(explanation);
            checkForMax();
            return questionResponse;
        }
    }

    private void checkForMax() {
        if (numberOfAnswed >= numberOfQuestions) {
            sendToProgressTracker();
        }
    }

    private void sendToProgressTracker() {
        float performance = (float) numberOfRight / (float) numberOfQuestions;
        this.progressUpdater.updateAllProgress(numberOfRight, performance);
    }

    private String questionRight(String explano) {
        numberOfRight++;
        return "Corrrect || " + explano;
    }

    private String questionWrong(String explano) {
        return "Incorrect " + " || " + explano;
    }
}
