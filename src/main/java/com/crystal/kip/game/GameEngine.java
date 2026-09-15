package com.crystal.kip.game;

import org.springframework.stereotype.Service;

import com.crystal.kip.content.Game;
import com.crystal.kip.content.Topic;
import com.crystal.kip.domain.User;

@Service
public class GameEngine {

    public GameEngine GameEngine() {
        return new GameEngine();
    }

    public GameSession startSession(User user, Game game, Topic topic) {
        return new GameSession(user, game, topic);
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