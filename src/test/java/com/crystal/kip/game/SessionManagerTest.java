package com.crystal.kip.game;

import static org.junit.jupiter.api.Assertions.*;

import com.crystal.kip.content.Game;
import com.crystal.kip.content.Topic;
import com.crystal.kip.domain.User;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    private SessionManager manager;
    private GameSession session;

    @BeforeEach
    void setUp() {
        manager = new SessionManager();
        session = new GameSession(new User(), new Game(), new Topic());
    }

    @Test
    void testAdd() {
        manager.add(session);

        assertEquals(session, manager.get(session.getId()));
    }

    @Test
    void testRemove() {
        manager.add(session);
        manager.remove(session.getId());

        assertNull(manager.get(session.getId()));
    }

    @Test
    void testGetNullGameSession() {
        assertNull(manager.get(UUID.randomUUID()));
    }
}
