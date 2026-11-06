package com.crystal.kip.service;

import com.crystal.kip.domain.GameProgress;
import com.crystal.kip.domain.GameSession;
import com.crystal.kip.repository.GameProgressRepository;
import com.crystal.kip.repository.GameSessionRepository;
import com.crystal.kip.security.SecurityUtils;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Read-only stats for the home dashboard. Only ever reads the signed-in user's own data.
 */
@Service
public class DashboardStatsService {

    // KIP's first legal content is Virginia law, so streak "days" use Eastern time.
    private static final ZoneId STREAK_TIME_ZONE = ZoneId.of("America/New_York");
    private static final int RECENT_ACTIVITY_LIMIT = 5;

    private final GameSessionRepository gameSessionRepository;
    private final GameProgressRepository gameProgressRepository;

    public DashboardStatsService(GameSessionRepository gameSessionRepository, GameProgressRepository gameProgressRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.gameProgressRepository = gameProgressRepository;
    }

    public record RecentActivity(Long id, String gameName, Instant completedAt) {}

    public record DashboardSummary(
        long gamesPlayed,
        Integer averagePerformance,
        int currentStreak,
        long weeklyPlays,
        String favoriteGame,
        List<RecentActivity> recentActivity
    ) {}

    @Transactional(readOnly = true)
    public DashboardSummary getSummary() {
        // /api/** already requires login, so this should never be empty here.
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No current user logged in"));

        // Newest first, so the first few are the "recent" ones.
        List<GameSession> sessions = gameSessionRepository.findByUser_DataUser_LoginOrderByCompletedAtDesc(login);
        List<GameProgress> progress = gameProgressRepository.findByUserProfile_DataUser_Login(login);

        List<RecentActivity> recent = sessions
            .stream()
            .filter(session -> session.getCompletedAt() != null)
            .limit(RECENT_ACTIVITY_LIMIT)
            .map(session -> new RecentActivity(session.getId(), getGameName(session), session.getCompletedAt()))
            .toList();

        long completedGames = sessions
            .stream()
            .filter(session -> session.getCompletedAt() != null)
            .count();

        LocalDate today = LocalDate.now(STREAK_TIME_ZONE);
        return new DashboardSummary(
            completedGames,
            calculateAveragePerformance(progress),
            calculateStreak(sessions, today),
            calculateWeeklyPlays(sessions, today),
            calculateFavoriteGame(sessions),
            recent
        );
    }

    /**
     * Completed games from today and the six previous calendar days, using Eastern time.
     * Several completed games in one day each count as a play.
     */
    static long calculateWeeklyPlays(List<GameSession> sessions, LocalDate today) {
        LocalDate firstDay = today.minusDays(6);
        return sessions
            .stream()
            .filter(session -> session.getCompletedAt() != null)
            .map(session -> session.getCompletedAt().atZone(STREAK_TIME_ZONE).toLocalDate())
            .filter(date -> !date.isBefore(firstDay) && !date.isAfter(today))
            .count();
    }

    /**
     * The most frequently completed game. Sessions are newest first from the repository,
     * so a tie goes to the game the user completed most recently.
     */
    static String calculateFavoriteGame(List<GameSession> sessions) {
        Map<Long, Long> playCounts = new LinkedHashMap<>();
        Map<Long, String> gameNames = new LinkedHashMap<>();

        for (GameSession session : sessions) {
            if (session.getCompletedAt() == null || session.getGame() == null || session.getGame().getId() == null) {
                continue;
            }
            Long gameId = session.getGame().getId();
            playCounts.merge(gameId, 1L, Long::sum);
            gameNames.putIfAbsent(gameId, session.getGame().getName());
        }

        Long favoriteId = null;
        long highestCount = 0;
        for (Map.Entry<Long, Long> entry : playCounts.entrySet()) {
            if (entry.getValue() > highestCount) {
                favoriteId = entry.getKey();
                highestCount = entry.getValue();
            }
        }
        return favoriteId == null ? null : gameNames.get(favoriteId);
    }

    private static String getGameName(GameSession session) {
        return session.getGame() == null ? "Unknown game" : session.getGame().getName();
    }

    /**
     * Performance is stored as 0..1 (questions right / questions asked, see Feedback.java).
     * Each GameProgress row stores a running average, so weighting by sessionsPlayed gives the
     * overall average across every session. Returns a whole-number percent, or null if nothing is recorded yet.
     */
    static Integer calculateAveragePerformance(List<GameProgress> gameProgress) {
        double weightedTotal = 0;
        long sessionTotal = 0;

        for (GameProgress progress : gameProgress) {
            Integer sessionsPlayed = progress.getSessionsPlayed();
            Float performance = progress.getPerformance();
            if (sessionsPlayed != null && sessionsPlayed > 0 && performance != null) {
                weightedTotal += performance * sessionsPlayed;
                sessionTotal += sessionsPlayed;
            }
        }

        // Null means "no performance recorded yet", not 0%.
        return sessionTotal == 0 ? null : (int) Math.round((100.0 * weightedTotal) / sessionTotal);
    }

    /**
     * Counts back day by day from today. If the user hasn't played yet today, the streak is still
     * alive as long as they played yesterday. Several games on the same day count as one day.
     */
    static int calculateStreak(List<GameSession> sessions, LocalDate today) {
        Set<LocalDate> playedDates = new HashSet<>();

        for (GameSession session : sessions) {
            Instant completedAt = session.getCompletedAt();
            if (completedAt != null) {
                playedDates.add(completedAt.atZone(STREAK_TIME_ZONE).toLocalDate());
            }
        }

        LocalDate day = playedDates.contains(today) ? today : today.minusDays(1);
        int streak = 0;
        while (playedDates.contains(day)) {
            streak++;
            day = day.minusDays(1);
        }
        return streak;
    }
}
