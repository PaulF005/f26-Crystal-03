package com.crystal.kip.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class GameSessionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static GameSession getGameSessionSample1() {
        return new GameSession().id(1L);
    }

    public static GameSession getGameSessionSample2() {
        return new GameSession().id(2L);
    }

    public static GameSession getGameSessionRandomSampleGenerator() {
        return new GameSession().id(longCount.incrementAndGet());
    }
}
