package com.crystal.kip.progress_update;

import com.crystal.kip.repository.LinkRepo.LinkConceptProgressRepository;
import com.crystal.kip.security.SecurityUtils;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProgressUpdaterConcept {

    private final LinkConceptProgressRepository linkConceptProgressRepository;
    private static final Logger log = LoggerFactory.getLogger(ProgressUpdaterConcept.class);

    public ProgressUpdaterConcept(LinkConceptProgressRepository linkConceptProgressRepository) {
        this.linkConceptProgressRepository = linkConceptProgressRepository;
    }

    public float updateConceptProgress(Long conceptId, int questionsRight) {
        String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No current user logged in"));

        int totalQuestions = this.linkConceptProgressRepository.getMaxQuestionsForRepo(username, conceptId);

        if (totalQuestions < 0) {
            log.error("Failed to read total questions from repo!");
            throw new IllegalArgumentException("Concept Progress has an error in its Max Questions!");
        }

        if (totalQuestions == 0) {
            log.error("Concept has no questions!");
            throw new IllegalArgumentException("Concept Progress cannot be updated with zero questions!");
        }

        double currentCompetency = this.linkConceptProgressRepository.getCurCompetencyCP(username, conceptId);

        if (currentCompetency < 0) {
            log.error("Failed to read current competency from repo!");
            throw new IllegalArgumentException("Concept Progress has an error in its Current Competency!");
        }

        double competencyBefore = currentCompetency;

        double percentDone = (double) questionsRight / totalQuestions;
        currentCompetency += percentDone;

        currentCompetency = Math.min(currentCompetency, 1.0);

        double improvement = currentCompetency - competencyBefore;

        Instant now = Instant.now();

        this.linkConceptProgressRepository.updateConceptProgressToDBCP(conceptId, (float) currentCompetency, (float) improvement, now);

        return (float) improvement;
    }
}
