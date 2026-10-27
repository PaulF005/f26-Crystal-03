package com.crystal.kip.progress_update;

import com.crystal.kip.repository.LinkRepo.LinkTopicProgressRepository;
import com.crystal.kip.security.SecurityUtils;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProgressUpdaterTopic {

    private final LinkTopicProgressRepository linkTopicProgressRepository;
    private static final Logger log = LoggerFactory.getLogger(ProgressUpdaterTopic.class);

    public ProgressUpdaterTopic(LinkTopicProgressRepository linkTopicProgressRepository) {
        this.linkTopicProgressRepository = linkTopicProgressRepository;
    }

    public void updateTopicProgress(Long topicId, float improvementToAdd) {
        String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No current user logged in"));

        double currentCompetency = this.linkTopicProgressRepository.getCurCompetencyTP(username, topicId);

        if (currentCompetency < 0.0) {
            log.error("Failed to read current competency from repo!");
            throw new IllegalArgumentException("Topic Progress has an error in its Current Competency!");
        }

        int maxConcepts = this.linkTopicProgressRepository.getAllTopicNumTP(topicId);

        if (maxConcepts < 0) {
            log.error("Failed to read max concepts from repo!");
            throw new IllegalArgumentException("Topic Progress has an error in its Max Concepts!");
        }

        if (maxConcepts == 0) {
            log.error("Failed to find any concepts for Topic!");
            throw new IllegalArgumentException("Topic Progress cannot be updated with zero Concepts!");
        }

        double percentDone = (double) improvementToAdd / maxConcepts;
        currentCompetency += percentDone;

        double marginOfError = 0.00001;

        if (currentCompetency >= 1.0 - marginOfError) {
            currentCompetency = 1.0;
            percentDone = 0.0;
        } else if (currentCompetency >= 0.99 - marginOfError) {
            currentCompetency = 1.0;
            percentDone = 0.01;
        }

        Instant now = Instant.now();

        this.linkTopicProgressRepository.updateTopicProgressToDB(topicId, (float) currentCompetency, (float) percentDone, now);
    }
}
