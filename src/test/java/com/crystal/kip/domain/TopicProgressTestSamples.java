package com.crystal.kip.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TopicProgressTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static TopicProgress getTopicProgressSample1() {
        return new TopicProgress().id(1L);
    }

    public static TopicProgress getTopicProgressSample2() {
        return new TopicProgress().id(2L);
    }

    public static TopicProgress getTopicProgressRandomSampleGenerator() {
        return new TopicProgress().id(longCount.incrementAndGet());
    }
}
