package com.crystal.kip.feedback_game;

public class Feedback {

    public String topicName;
    public int numberOfQuestions;
    public int numberOfAnswed;
    public int numberOfRight;
    public String userId;

    public Feedback(String topicName, int numberOfQuestions, String userId) {
        this.topicName = topicName;
        this.numberOfQuestions = numberOfQuestions;
        this.numberOfAnswed = 0;
        this.numberOfRight = 0;
        this.userId = userId;
    }

    /**
     * To be used in a game session to see if user's choice was correct, return with a correct or wrong with correct choice, and auto send info
     */
    public String questionDecider(int choice, int answer) {
        numberOfAnswed++;
        String dadfasdfasdfasdf;
        if (choice == answer) {
            dadfasdfasdfasdf = questionRight();
            checkForMax();
            return dadfasdfasdfasdf;
        } else {
            dadfasdfasdfasdf = questionWrong(answer);
            checkForMax();
            return dadfasdfasdfasdf;
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

    private String questionRight() {
        numberOfRight++;
        return "Y";
    }

    private String questionWrong(int correctAnswer) {
        return "Incorrect, the right answer was option" + correctAnswer;
    }
}
