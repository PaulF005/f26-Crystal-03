package com.crystal.kip.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class GameProgressTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static GameProgress getGameProgressSample1() {
        return new GameProgress().id(1L).sessionsPlayed(1).evidenceCount(1);
    }

    public static GameProgress getGameProgressSample2() {
        return new GameProgress().id(2L).sessionsPlayed(2).evidenceCount(2);
    }

    public static GameProgress getGameProgressRandomSampleGenerator() {
        return new GameProgress()
            .id(longCount.incrementAndGet())
            .sessionsPlayed(intCount.incrementAndGet())
            .evidenceCount(intCount.incrementAndGet());
    }
}
