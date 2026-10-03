package com.crystal.kip.progress_update;

import com.crystal.kip.repository.LinkRepo.LinkConcpetProgressRepository;
import com.crystal.kip.security.SecurityUtils;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgressUpdaterConcept {

    private final LinkConcpetProgressRepository linkConcpetProgressRepository;
    private static final Logger log = LoggerFactory.getLogger(ProgressUpdaterConcept.class);

    public ProgressUpdaterConcept(LinkConcpetProgressRepository linkConcpetProgressRepository) {
        this.linkConcpetProgressRepository = linkConcpetProgressRepository;
    }

    public void updateTopicProgress(int questionsRight) {
        String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No current user logged in"));

        int totalQuestions = this.linkConcpetProgressRepository.getMaxQuestionsForRepo(username);
        if (totalQuestions < 0) {
            log.error("Failed to read total questions from repo!");
            throw new IllegalArgumentException("Concept Progress has an error in its Max Questions!");
        }

        double currentCompantency = this.linkConcpetProgressRepository.getCurCompantencyCP(username);
        if (currentCompantency < 0) {
            log.error("Failed to read current compantency from repo!");
            throw new IllegalArgumentException("Concept Progress has an error in its Current Compantency!");
        }

        double percentDone = (double) questionsRight / totalQuestions;
        currentCompantency += percentDone;

        //Close enough!!
        if (currentCompantency >= 0.99) {
            currentCompantency = 1.0;
        }

        Instant now = Instant.now();
        this.linkConcpetProgressRepository.updateConceptProgressToDB((float) currentCompantency, now);
    }
}
