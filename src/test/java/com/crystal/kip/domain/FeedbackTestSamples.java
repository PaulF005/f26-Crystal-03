package com.crystal.kip.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FeedbackTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Feedback getFeedbackSample1() {
        return new Feedback().id(1L).feedback("feedback1").explanation("explanation1");
    }

    public static Feedback getFeedbackSample2() {
        return new Feedback().id(2L).feedback("feedback2").explanation("explanation2");
    }

    public static Feedback getFeedbackRandomSampleGenerator() {
        return new Feedback()
            .id(longCount.incrementAndGet())
            .feedback(UUID.randomUUID().toString())
            .explanation(UUID.randomUUID().toString());
    }
}
