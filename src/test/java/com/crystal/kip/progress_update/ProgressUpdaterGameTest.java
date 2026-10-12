package com.crystal.kip.progress_update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    /**
     * Creates a mock database entry for a LinkGameProgressRepository to test against
     */
    @Mock
    private LinkGameProgressRepository linkGameProgressRepository;

    @Captor
    private ArgumentCaptor<Float> performanceCaptor;

    @Captor
    private ArgumentCaptor<Integer> sessionsPlayedCaptor;

    @Captor
    private ArgumentCaptor<Instant> nowCaptor;

    private ProgressUpdaterGame progressUpdaterGame;
    private MockedStatic<SecurityUtils> mockedSecurtiyUtils;
    private static final String TESTUSER = "testUser";

    /**
     * Sets up a ProgressUpdaterConcept and opens the Mock
     */
    @BeforeEach
    void setUp() {
        progressUpdaterGame = new ProgressUpdaterGame(linkGameProgressRepository);
        mockedSecurtiyUtils = mockStatic(SecurityUtils.class);
    }

    /**
     * Closes the Mock
     */
    @AfterEach
    void tearDown() {
        if (mockedSecurtiyUtils != null) {
            mockedSecurtiyUtils.close();
        }
    }

    //TESTS BELOW

    /**
     * Tests to see if the update function works
     */
    @Test
    void updateGameProgressSuccess() {
        mockedSecurtiyUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkGameProgressRepository.getSessionsPlayedGP(TESTUSER)).thenReturn(67);
        when(linkGameProgressRepository.getPerformanceGP(TESTUSER)).thenReturn(0.89);

        progressUpdaterGame.updateGameProgress(0.95f);

        verify(linkGameProgressRepository).updateGameProgressToDb(
            sessionsPlayedCaptor.capture(),
            performanceCaptor.capture(),
            nowCaptor.capture()
        );
        assertEquals(68, sessionsPlayedCaptor.getValue());
        assertEquals(0.89088, performanceCaptor.getValue(), .00001);
        assertNotNull(nowCaptor.getValue());
    }

    /**
     * Tests to see if the intal game progress is updated with no previous ones
     */
    @Test
    void updateGameProgressWith0SessionsPlayed() {
        mockedSecurtiyUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkGameProgressRepository.getSessionsPlayedGP(TESTUSER)).thenReturn(0);
        when(linkGameProgressRepository.getPerformanceGP(TESTUSER)).thenReturn(0.0);

        progressUpdaterGame.updateGameProgress(0.95f);

        verify(linkGameProgressRepository).updateGameProgressToDb(
            sessionsPlayedCaptor.capture(),
            performanceCaptor.capture(),
            nowCaptor.capture()
        );
        assertEquals(1, sessionsPlayedCaptor.getValue());
        assertEquals(0.95, performanceCaptor.getValue(), .001);
        assertNotNull(nowCaptor.getValue());
    }

    /**
     * Tests to see if throws execption when no user is logged in
     */
    @Test
    void updateGameProgressExecptionNoUser() {
        mockedSecurtiyUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> progressUpdaterGame.updateGameProgress(0.95f));
        verifyNoInteractions(linkGameProgressRepository);
    }

    /**
     * Tests to see if throws execption when sessionsPlayed has a bad read
     */
    @Test
    void updateGameProgressExceptionBadReadSessionsPlayed() {
        mockedSecurtiyUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkGameProgressRepository.getSessionsPlayedGP(TESTUSER)).thenReturn(-1);

        assertThrows(IllegalArgumentException.class, () -> progressUpdaterGame.updateGameProgress(.95f));
    }

    /**
     * Tests to see if throws execption when performance has a bad read
     */
    @Test
    void updateConceptProgressExceptionBadReadCurrentCompetency() {
        mockedSecurtiyUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkGameProgressRepository.getSessionsPlayedGP(TESTUSER)).thenReturn(100);
        when(linkGameProgressRepository.getPerformanceGP(TESTUSER)).thenReturn(-1.0);

        assertThrows(IllegalArgumentException.class, () -> progressUpdaterGame.updateGameProgress(.95f));
    }
}
