package com.crystal.kip.progress_update;

import com.crystal.kip.repository.LinkRepo.LinkGameProgressRepository;
import com.crystal.kip.security.SecurityUtils;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgressUpdaterGame {

    private final LinkGameProgressRepository linkGameProgressRepository;
    private static final Logger log = LoggerFactory.getLogger(ProgressUpdaterGame.class);

    public ProgressUpdaterGame(LinkGameProgressRepository linkGameProgressRepository) {
        this.linkGameProgressRepository = linkGameProgressRepository;
    }

    public void updateGameProgress(float performance) {
        String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No current user logged in"));

        int sessionsPlayed = this.linkGameProgressRepository.getSessionsPlayed(username);
        if (sessionsPlayed < 0) {
            log.error("Failed to read sessions played from repo!");
            throw new IllegalArgumentException("Game Progress has an error in its session played!");
        }

        double dbPerformance = this.linkGameProgressRepository.getPerformance(username);
        if (dbPerformance < 0.0) {
            log.error("Failed to read performance from repo!");
            throw new IllegalArgumentException("Game Progress has an error in its performance!");
        }

        double performanceOut;
        if (sessionsPlayed == 0) {
            performanceOut = performance;
        } else {
            performanceOut = (dbPerformance * (float) sessionsPlayed + performance) / (float) (sessionsPlayed + 1);
        }

        Instant now = Instant.now();
        this.linkGameProgressRepository.updateGameProgressToDb(sessionsPlayed + 1, (float) performanceOut, now);
    }
}
