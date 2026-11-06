package com.crystal.kip.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.GameProgress;
import com.crystal.kip.domain.GameSession;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Plain unit tests (no Spring, no database) for the dashboard math.
 */
class DashboardStatsServiceTest {

    // A fixed "today" so the tests don't depend on when they run.
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 31);

    private static GameSession sessionAt(String instant) {
        return new GameSession().completedAt(Instant.parse(instant));
    }

    @Test
    void streakIsZeroWithNoSessions() {
        assertThat(DashboardStatsService.calculateStreak(List.of(), TODAY)).isZero();
    }

    @Test
    void multipleGamesOnSameDayCountOnce() {
        // 10am and 3pm Eastern on Oct 31 (EDT is UTC-4)
        List<GameSession> sessions = List.of(sessionAt("2026-10-31T14:00:00Z"), sessionAt("2026-10-31T19:00:00Z"));
        assertThat(DashboardStatsService.calculateStreak(sessions, TODAY)).isEqualTo(1);
    }

    @Test
    void streakStillCountsIfLastPlayedYesterday() {
        List<GameSession> sessions = List.of(sessionAt("2026-10-30T16:00:00Z"), sessionAt("2026-10-29T16:00:00Z"));
        assertThat(DashboardStatsService.calculateStreak(sessions, TODAY)).isEqualTo(2);
    }

    @Test
    void streakBreaksAfterAMissedDay() {
        // Played Oct 31 and Oct 29, skipped Oct 30
        List<GameSession> sessions = List.of(sessionAt("2026-10-31T16:00:00Z"), sessionAt("2026-10-29T16:00:00Z"));
        assertThat(DashboardStatsService.calculateStreak(sessions, TODAY)).isEqualTo(1);
    }

    @Test
    void lateNightGameUsesEasternDateNotUtcDate() {
        // 11:30pm Eastern on Oct 30 is already Oct 31 in UTC. It should count as "yesterday".
        List<GameSession> sessions = List.of(sessionAt("2026-10-31T03:30:00Z"));
        assertThat(DashboardStatsService.calculateStreak(sessions, TODAY)).isEqualTo(1);
        assertThat(DashboardStatsService.calculateStreak(sessions, LocalDate.of(2026, 11, 1))).isZero();
    }

    @Test
    void weeklyPlaysCountsCompletionsInLastSevenCalendarDays() {
        List<GameSession> sessions = List.of(
            sessionAt("2026-10-25T15:00:00Z"), // first day included
            sessionAt("2026-10-31T14:00:00Z"),
            sessionAt("2026-10-31T19:00:00Z"), // second game today counts too
            sessionAt("2026-10-24T15:00:00Z"), // too old
            sessionAt("2026-11-01T16:00:00Z") // future date
        );
        assertThat(DashboardStatsService.calculateWeeklyPlays(sessions, TODAY)).isEqualTo(3);
    }

    @Test
    void weeklyPlaysIgnoresUnfinishedSessionsAndUsesEasternTime() {
        List<GameSession> sessions = List.of(
            sessionAt("2026-10-25T03:30:00Z"), // Oct 24 in Eastern time, too old
            sessionAt("2026-11-01T03:30:00Z"), // Oct 31 in Eastern time, included
            new GameSession() // incomplete
        );
        assertThat(DashboardStatsService.calculateWeeklyPlays(sessions, TODAY)).isEqualTo(1);
        assertThat(DashboardStatsService.calculateWeeklyPlays(List.of(), TODAY)).isZero();
    }

    private static GameSession playedGame(long gameId, String name, String completedAt) {
        return new GameSession().game(new Game().id(gameId).name(name)).completedAt(Instant.parse(completedAt));
    }

    @Test
    void favoriteGameIsMostFrequentlyCompletedGame() {
        List<GameSession> sessions = List.of(
            playedGame(2L, "Choose the Outcome", "2026-10-31T15:00:00Z"),
            playedGame(1L, "Fact or Fiction?", "2026-10-30T15:00:00Z"),
            playedGame(1L, "Fact or Fiction?", "2026-10-29T15:00:00Z"),
            new GameSession().game(new Game().id(2L).name("Choose the Outcome")) // not completed
        );
        assertThat(DashboardStatsService.calculateFavoriteGame(sessions)).isEqualTo("Fact or Fiction?");
    }

    @Test
    void favoriteGameTieGoesToMostRecentlyCompleted() {
        // Same count, but sessions are supplied newest first (as returned by the repository).
        List<GameSession> sessions = List.of(
            playedGame(2L, "Choose the Outcome", "2026-10-31T15:00:00Z"),
            playedGame(1L, "Fact or Fiction?", "2026-10-30T15:00:00Z")
        );
        assertThat(DashboardStatsService.calculateFavoriteGame(sessions)).isEqualTo("Choose the Outcome");
    }

    @Test
    void favoriteGameIsNullWithNoCompletedGame() {
        assertThat(DashboardStatsService.calculateFavoriteGame(List.of())).isNull();
        assertThat(DashboardStatsService.calculateFavoriteGame(List.of(new GameSession()))).isNull();
    }

    @Test
    void averageIsNullWhenNothingRecorded() {
        assertThat(DashboardStatsService.calculateAveragePerformance(List.of())).isNull();
        assertThat(
            DashboardStatsService.calculateAveragePerformance(List.of(new GameProgress().sessionsPlayed(0).performance(0.9f)))
        ).isNull();
    }

    @Test
    void averageIsWeightedBySessionsPlayed() {
        // 3 sessions averaging 100% + 1 session at 0% = 75% overall
        List<GameProgress> progress = List.of(
            new GameProgress().sessionsPlayed(3).performance(1.0f),
            new GameProgress().sessionsPlayed(1).performance(0.0f)
        );
        assertThat(DashboardStatsService.calculateAveragePerformance(progress)).isEqualTo(75);
    }
}
