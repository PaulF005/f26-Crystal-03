package com.crystal.kip.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.domain.UserProfile;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    private SessionManager manager;
    private GameActive active;

    @BeforeEach
    void setUp() {
        manager = new SessionManager();
        active = new GameActive(new UserProfile(), new Game(), new Topic());
    }

    @Test
    void testAdd() {
        manager.add(active);

        assertEquals(active, manager.get(active.getId()));
    }

    @Test
    void testRemove() {
        manager.add(active);
        manager.remove(active.getId());

        assertNull(manager.get(active.getId()));
    }

    @Test
    void testGetNullGameSession() {
        assertNull(manager.get(UUID.randomUUID()));
    }
}
