package com.crystal.kip.game;

import java.util.List;

import com.crystal.kip.content.Game;
import com.crystal.kip.content.Topic;

class GameSession {

    Game game;
    Topic topic;

    List<GameScenario> scenarios;
    GameScenario currentScenario;

    public GameSession(Game game) {
        //TODO Auto-generated constructor stub
    }

    public Game getGame() {
        return game;
    }

    public Topic getTopic() {
        return topic;
    }
}