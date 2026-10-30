package com.crystal.kip.service;

import com.crystal.kip.domain.GameSession;
import com.crystal.kip.repository.GameSessionRepository;
import com.crystal.kip.security.SecurityUtils;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardStatsService {

    private static final ZoneId STREAK_TIME_ZONE = ZoneId.of("America/New_York");

    private final GameSessionRepository gameSessionRepository;

    public DashboardStatsService(GameSessionRepository gameSessionRepository) {
        this.gameSessionRepository = gameSessionRepository;
    }

    @Transactional(readOnly = true)
    public int getCurrentStreak() {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();

        if (currentUserLogin.isEmpty()) {
            return 0;
        }

        List<GameSession> sessions = gameSessionRepository.findAll();
        List<GameSession> userSessions = new ArrayList<>();

        for (GameSession session : sessions) {
            if (
                session.getUser() != null &&
                session.getUser().getDataUser() != null &&
                currentUserLogin.get().equals(session.getUser().getDataUser().getLogin())
            ) {
                userSessions.add(session);
            }
        }

        Set<LocalDate> completionDates = new HashSet<>();

        for (GameSession session : userSessions) {
            if (session.getCompletedAt() != null) {
                LocalDate completedDate = session.getCompletedAt().atZone(STREAK_TIME_ZONE).toLocalDate();

                completionDates.add(completedDate);
            }
        }

        if (completionDates.isEmpty()) {
            return 0;
        }

        List<LocalDate> sortedDates = new ArrayList<>(completionDates);
        sortedDates.sort(Comparator.reverseOrder());

        LocalDate today = LocalDate.now(STREAK_TIME_ZONE);
        LocalDate mostRecentDate = sortedDates.get(0);

        if (mostRecentDate.isBefore(today.minusDays(1))) {
            return 0;
        }

        int streak = 1;
        LocalDate expectedDate = mostRecentDate.minusDays(1);

        for (int i = 1; i < sortedDates.size(); i++) {
            LocalDate currentDate = sortedDates.get(i);

            if (currentDate.equals(expectedDate)) {
                streak++;
                expectedDate = expectedDate.minusDays(1);
            } else {
                break;
            }
        }

        return streak;
    }
}
