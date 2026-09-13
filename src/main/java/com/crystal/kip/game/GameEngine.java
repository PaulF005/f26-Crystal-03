package com.crystal.kip.game;

import com.crystal.kip.content.Game;
import com.crystal.kip.domain.User;

public class GameEngine {

    public GameSession startSession(User user, Game game) {
        GameSession session = new GameSession(game);

        return session;
    }

    private void endSession(GameSession session) {
        
    }

    public void completeSession(GameSession session) {
        // commit results to db
        endSession(session);
    }

    public void abandonSession(GameSession session) {
        // discard results
        endSession(session);
    }

    public void startScenario(GameSession session) {

    }
    
    public void completeScenario(GameSession session) {
        
    }

    public void advanceScenario(GameSession session) {
        
    }

    public void advanceStage(GameSession session) {
        
    }
    
    public void selectAnswer(GameSession session, int answerIndex) {
        submitAnswer(session);
    }
    
    public void submitAnswer(GameSession session) {
        
    }
    
}