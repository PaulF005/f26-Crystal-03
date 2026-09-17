package com.crystal.kip.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class StageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Stage getStageSample1() {
        return new Stage().id(1L);
    }

    public static Stage getStageSample2() {
        return new Stage().id(2L);
    }

    public static Stage getStageRandomSampleGenerator() {
        return new Stage().id(longCount.incrementAndGet());
    }
}
