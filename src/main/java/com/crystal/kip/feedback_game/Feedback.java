package com.crystal.kip.feedback_game;

/**
 * Used to determine whether a question was answered correctly
 * and return the appropriate explanation
 */
public class Feedback {

    /**
     * To be used in a game session to see if the user's choice was correct
     * and return feedback with an explanation
     */
    public String questionDecider(int choice, int answer, String explanation) {
        if (choice == answer) {
            return questionRight(explanation);
        } else {
            return questionWrong(explanation);
        }
    }

    private String questionRight(String explanation) {
        return "Correct || " + explanation;
    }

    private String questionWrong(String explanation) {
        return "Incorrect || " + explanation;
    }
}
