package com.crystal.kip.game;

import com.crystal.kip.domain.User;
import com.crystal.kip.content.Game;
import com.crystal.kip.content.Topic;

import java.util.List;
import java.util.UUID;

class GameSession {

    UUID id;
    User user;
    
    Game game;
    Topic topic;

    List<GameScenario> scenarios;
    GameScenario currentScenario;

    public GameSession(User user, Game game, Topic topic) {
        this.user = user;
        this.game = game;
        this.topic = topic;

        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public Topic getTopic() {
        return topic;
    }
}