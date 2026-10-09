package com.crystal.kip.progress_update;

import com.crystal.kip.repository.LinkRepo.LinkConcpetProgressRepository;
import com.crystal.kip.security.SecurityUtils;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProgressUpdaterConcept {

    private final LinkConcpetProgressRepository linkConcpetProgressRepository;
    private static final Logger log = LoggerFactory.getLogger(ProgressUpdaterConcept.class);

    public ProgressUpdaterConcept(LinkConcpetProgressRepository linkConcpetProgressRepository) {
        this.linkConcpetProgressRepository = linkConcpetProgressRepository;
    }

    public void updateConceptProgress(int questionsRight) {
        String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No current user logged in"));

        int totalQuestions = this.linkConcpetProgressRepository.getMaxQuestionsForRepo(username);
        if (totalQuestions < 0) {
            log.error("Failed to read total questions from repo!");
            throw new IllegalArgumentException("Concept Progress has an error in its Max Questions!");
        }

        double currentCompetency = this.linkConcpetProgressRepository.getCurCompentencyCP(username);
        if (currentCompetency < 0) {
            log.error("Failed to read current competency from repo!");
            throw new IllegalArgumentException("Concept Progress has an error in its Current Competency!");
        }

        double percentDone = (double) questionsRight / totalQuestions;
        currentCompetency += percentDone;

        //Close enough!! Will not indicate an increase in imporvement if there is no increase in Competency
        if (currentCompetency >= 0.99 && currentCompetency <= 1.0) {
            currentCompetency = 1.0;
            percentDone = 0.01;
        } else if (currentCompetency >= 1.0) {
            currentCompetency = 1.0;
            percentDone = 0.0;
        }

        Instant now = Instant.now();
        this.linkConcpetProgressRepository.updateConceptProgressToDBCP((float) currentCompetency, (float) percentDone, now);
    }

    public float getImprovement() {
        String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No current user logged in"));

        double improvementToAdd = this.linkConcpetProgressRepository.getImprovementCP(username);
        if (improvementToAdd < 0) {
            log.error("Failed to read current improvement from repo!");
            throw new IllegalArgumentException("Concept Progress has an error in its current imporvement!");
        }

        return (float) improvementToAdd;
    }
}
