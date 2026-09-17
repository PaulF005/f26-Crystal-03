package com.crystal.kip.feedback_game;

/**
 * Used to return question if it is correct or wrong and an explanation.
 * Also keeps track of number of questions ansered and correct to send to progress tracker
 * Feedback
 */
public class Feedback {

    public String topicName;
    public String scenario;
    public int numberOfQuestions;
    public int numberOfAnswed;
    public int numberOfRight;
    public String userId;

    /**
     *
     * @param topicName Name of the topic
     * @param numberOfQuestions Number of questions for given scenario
     * @param userId Name of userId
     */
    public Feedback(String topicName, String scenario, int numberOfQuestions, String userId) {
        this.topicName = topicName;
        this.scenario = scenario;
        this.numberOfQuestions = numberOfQuestions;
        this.numberOfAnswed = 0;
        this.numberOfRight = 0;
        this.userId = userId;
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
            questionResponse = questionWrong(answer, explanation);
            checkForMax();
            return questionResponse;
        }
    }

    private void checkForMax() {
        if (numberOfAnswed >= numberOfQuestions) {
            sendToProgressTracker();
        }
    }

    /**
     * TODO Need to cordinate what progress tracker and db will need for update
     */
    private void sendToProgressTracker() {
        throw new UnsupportedOperationException("Unimplemented method 'sendToProgressTracker'");
    }

    private String questionRight(String explano) {
        numberOfRight++;
        return "Y || " + explano;
    }

    private String questionWrong(int correctAnswer, String explano) {
        return "Incorrect, the right answer was option " + correctAnswer + " || " + explano;
    }
}
