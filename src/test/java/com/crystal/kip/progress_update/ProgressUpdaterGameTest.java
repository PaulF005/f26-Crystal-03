package com.crystal.kip.progress_update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.crystal.kip.repository.LinkRepo.LinkGameProgressRepository;
import com.crystal.kip.security.SecurityUtils;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProgressUpdaterGameTest {

    @Mock
    private LinkGameProgressRepository linkGameProgressRepository;

    @Captor
    private ArgumentCaptor<Float> performanceCaptor;

    @Captor
    private ArgumentCaptor<Integer> sessionsPlayedCaptor;

    @Captor
    private ArgumentCaptor<Instant> nowCaptor;

    private ProgressUpdaterGame progressUpdaterGame;
    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    private static final String TESTUSER = "testUser";
    private static final Long GAME_ID = 31L;

    @BeforeEach
    void setUp() {
        progressUpdaterGame = new ProgressUpdaterGame(linkGameProgressRepository);
        mockedSecurityUtils = mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedSecurityUtils != null) {
            mockedSecurityUtils.close();
        }
    }

    @Test
    void updateGameProgressSuccess() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkGameProgressRepository.getSessionsPlayedGP(TESTUSER, GAME_ID)).thenReturn(67);
        when(linkGameProgressRepository.getPerformanceGP(TESTUSER, GAME_ID)).thenReturn(0.89);

        progressUpdaterGame.updateGameProgress(GAME_ID, 0.95f);

        verify(linkGameProgressRepository).getSessionsPlayedGP(TESTUSER, GAME_ID);
        verify(linkGameProgressRepository).getPerformanceGP(TESTUSER, GAME_ID);
        verify(linkGameProgressRepository).updateGameProgressToDb(
            eq(GAME_ID),
            sessionsPlayedCaptor.capture(),
            performanceCaptor.capture(),
            nowCaptor.capture()
        );

        assertEquals(68, sessionsPlayedCaptor.getValue());
        assertEquals(0.89088, performanceCaptor.getValue(), 0.00001);
        assertNotNull(nowCaptor.getValue());
    }

    @Test
    void updateGameProgressWith0SessionsPlayed() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkGameProgressRepository.getSessionsPlayedGP(TESTUSER, GAME_ID)).thenReturn(0);
        when(linkGameProgressRepository.getPerformanceGP(TESTUSER, GAME_ID)).thenReturn(0.0);

        progressUpdaterGame.updateGameProgress(GAME_ID, 0.95f);

        verify(linkGameProgressRepository).updateGameProgressToDb(
            eq(GAME_ID),
            sessionsPlayedCaptor.capture(),
            performanceCaptor.capture(),
            nowCaptor.capture()
        );

        assertEquals(1, sessionsPlayedCaptor.getValue());
        assertEquals(0.95, performanceCaptor.getValue(), 0.001);
        assertNotNull(nowCaptor.getValue());
    }

    @Test
    void updateGameProgressExceptionNoUser() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> progressUpdaterGame.updateGameProgress(GAME_ID, 0.95f));

        verifyNoInteractions(linkGameProgressRepository);
    }

    @Test
    void updateGameProgressExceptionBadReadSessionsPlayed() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkGameProgressRepository.getSessionsPlayedGP(TESTUSER, GAME_ID)).thenReturn(-1);

        assertThrows(IllegalArgumentException.class, () -> progressUpdaterGame.updateGameProgress(GAME_ID, 0.95f));
    }

    @Test
    void updateGameProgressExceptionBadReadPerformance() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkGameProgressRepository.getSessionsPlayedGP(TESTUSER, GAME_ID)).thenReturn(100);
        when(linkGameProgressRepository.getPerformanceGP(TESTUSER, GAME_ID)).thenReturn(-1.0);

        assertThrows(IllegalArgumentException.class, () -> progressUpdaterGame.updateGameProgress(GAME_ID, 0.95f));
    }
}
