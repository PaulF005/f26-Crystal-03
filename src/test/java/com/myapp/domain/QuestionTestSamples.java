package com.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class QuestionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Question getQuestionSample1() {
        return new Question().id(1L).question("question1");
    }

    public static Question getQuestionSample2() {
        return new Question().id(2L).question("question2");
    }

    public static Question getQuestionRandomSampleGenerator() {
        return new Question().id(longCount.incrementAndGet()).question(UUID.randomUUID().toString());
    }
}
