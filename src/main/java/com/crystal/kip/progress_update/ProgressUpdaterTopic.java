package com.crystal.kip.progress_update;

import com.crystal.kip.repository.LinkRepo.LinkTopicProgressRepository;
import com.crystal.kip.security.SecurityUtils;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgressUpdaterTopic {

    private final LinkTopicProgressRepository linkTopicProgressRepository;
    private static final Logger log = LoggerFactory.getLogger(ProgressUpdaterTopic.class);

    public ProgressUpdaterTopic(LinkTopicProgressRepository linkTopicProgressRepository) {
        this.linkTopicProgressRepository = linkTopicProgressRepository;
    }

    public void updateTopicProgress(float improvementToAdd) {
        String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No current user logged in"));

        double currentCompantency = this.linkTopicProgressRepository.getCurCompantencyTP(username);
        if (currentCompantency < 0.0) {
            log.error("Failed to read current compantency from repo!");
            throw new IllegalArgumentException("Concept Progress has an error in its Current Compantency!");
        }

        int maxConcepts = this.linkTopicProgressRepository.getAllTopicNum(username);
        if (maxConcepts < 0) {
            log.error("Failed to read max concepts from repo!");
            throw new IllegalArgumentException("Topic Progress has an error in its Max Questions!");
        }
        if (maxConcepts == 0) {
            log.error("Failed to have any concepts from repo!");
            throw new IllegalArgumentException("Topic Progress has an error in its Max Questions!");
        }

        if (currentCompantency >= 0.99) {
            currentCompantency = 1.0;
        }

        double percentDone = (double) improvementToAdd / (double) maxConcepts;
        currentCompantency += percentDone;
        Instant now = Instant.now();
        this.linkTopicProgressRepository.updateTopicProgressToDB((float) currentCompantency, now);
    }
}
