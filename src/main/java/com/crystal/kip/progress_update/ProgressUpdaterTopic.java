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

    public void updateTopicProgress(float improvementToAdd) {
        String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No current user logged in"));

        double currentCompetency = this.linkTopicProgressRepository.getCurCompetencyTP(username);
        if (currentCompetency < 0.0) {
            log.error("Failed to read current compantency from repo!");
            throw new IllegalArgumentException("Concept Progress has an error in its Current Compantency!");
        }

        int maxConcepts = this.linkTopicProgressRepository.getAllTopicNumTP(username);
        if (maxConcepts < 0) {
            log.error("Failed to read max concepts from repo!");
            throw new IllegalArgumentException("Topic Progress has an error in its Max Questions!");
        }
        if (maxConcepts == 0) {
            log.error("Failed to have any concepts from repo!");
            throw new IllegalArgumentException("Topic Progress has an error in its Max Questions!");
        }

        double percentDone = (double) improvementToAdd / (double) maxConcepts;
        currentCompetency += percentDone;

        double marginOfError = 0.00001;
        //Close enough (Hate Floating Point Errors)!! Will not indicate an increase in imporvement if there is no increase in Competency
        if (currentCompetency >= 1.0 - marginOfError) {
            currentCompetency = 1.0;
            percentDone = 0.0;
        } else if (currentCompetency >= 0.99 - marginOfError) {
            currentCompetency = 1.0;
            percentDone = 0.01;
        }

        Instant now = Instant.now();
        this.linkTopicProgressRepository.updateTopicProgressToDB((float) currentCompetency, (float) percentDone, now);
    }
}
