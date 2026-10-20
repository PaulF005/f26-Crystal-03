package com.crystal.kip.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SessionManager {

    private final Map<UUID, GameActive> sessions = new HashMap<>();

    public void add(GameActive session) {
        sessions.put(session.getId(), session);
    }

    public GameActive get(UUID sessionId) {
        return sessions.get(sessionId);
    }

    public void remove(UUID sessionId) {
        sessions.remove(sessionId);
    }
}
