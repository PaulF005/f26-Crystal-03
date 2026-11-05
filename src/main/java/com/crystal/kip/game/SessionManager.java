package com.crystal.kip.game;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class SessionManager {

    private final Map<UUID, GameActive> sessions = new ConcurrentHashMap<>();

    public void add(GameActive session) {
        sessions.put(session.getId(), session);
    }

    public GameActive get(UUID sessionId) {
        return sessions.get(sessionId);
    }

    public boolean contains(UUID sessionId) {
        return sessions.containsKey(sessionId);
    }

    public void remove(UUID sessionId) {
        sessions.remove(sessionId);
    }
}
