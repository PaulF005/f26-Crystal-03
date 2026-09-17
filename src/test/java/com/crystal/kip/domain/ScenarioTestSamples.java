package com.crystal.kip.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ScenarioTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Scenario getScenarioSample1() {
        return new Scenario().id(1L).name("name1");
    }

    public static Scenario getScenarioSample2() {
        return new Scenario().id(2L).name("name2");
    }

    public static Scenario getScenarioRandomSampleGenerator() {
        return new Scenario().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
