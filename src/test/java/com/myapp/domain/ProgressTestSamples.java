package com.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ProgressTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Progress getProgressSample1() {
        return new Progress().id(1L);
    }

    public static Progress getProgressSample2() {
        return new Progress().id(2L);
    }

    public static Progress getProgressRandomSampleGenerator() {
        return new Progress().id(longCount.incrementAndGet());
    }
}
