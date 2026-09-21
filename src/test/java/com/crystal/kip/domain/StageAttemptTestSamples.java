package com.crystal.kip.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class StageAttemptTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static StageAttempt getStageAttemptSample1() {
        return new StageAttempt().id(1L);
    }

    public static StageAttempt getStageAttemptSample2() {
        return new StageAttempt().id(2L);
    }

    public static StageAttempt getStageAttemptRandomSampleGenerator() {
        return new StageAttempt().id(longCount.incrementAndGet());
    }
}
