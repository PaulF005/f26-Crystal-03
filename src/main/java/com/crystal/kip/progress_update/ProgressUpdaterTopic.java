package com.crystal.kip.progress_update;

import com.crystal.kip.repository.LinkTopicProgressRepository;
import com.crystal.kip.security.SecurityUtils;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProgressUpdaterTopic {

    private final LinkTopicProgressRepository topicProgressRepository;
    private static final Logger log = LoggerFactory.getLogger(ProgressUpdaterTopic.class);

    public ProgressUpdaterTopic(LinkTopicProgressRepository topicProgressRepository) {
        this.topicProgressRepository = topicProgressRepository;
    }

    public void updateTopicProgress(int questionsRight) {
        String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No current user logged in"));
        int totalQuestions = this.topicProgressRepository.getMaxQuestionsForRepo(username);
        if (totalQuestions < 0) {
            log.error("Failed to read total questions from repo!");
            throw new IllegalArgumentException("Topic Progress has an error in its Max Questions!");
        }

        double currentCompantency = this.topicProgressRepository.getCurCompantency(username);
        if (currentCompantency < 0) {
            log.error("Failed to read current compantency from repo!");
            throw new IllegalArgumentException("Topic Progress has an error in its Current Compantency!");
        }

        if (currentCompantency >= 0.99) {
            log.debug("Update skipped, at max compantency!");
            return;
        }

        double percentDone = (double) questionsRight / totalQuestions;
        currentCompantency += percentDone;

        //Close enough!!
        if (currentCompantency >= 0.99) {
            currentCompantency = 1.0;
        }

        Instant now = Instant.now();
        this.topicProgressRepository.updateTopicProgressToDB((float) currentCompantency, now);
    }
}
