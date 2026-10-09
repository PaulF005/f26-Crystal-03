package com.crystal.kip.progress_update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.crystal.kip.repository.LinkRepo.LinkConcpetProgressRepository;
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
public class ProgressUpdaterConceptTest {

    /**
     * Creates a mock database entry for a LinkConcpetProgressRepository to test against
     */
    @Mock
    private LinkConcpetProgressRepository linkConcpetProgressRepository;

    @Captor
    private ArgumentCaptor<Float> competencyCaptor;

    @Captor
    private ArgumentCaptor<Float> improvementCaptor;

    @Captor
    private ArgumentCaptor<Instant> nowCaptor;

    private ProgressUpdaterConcept progressUpdaterConcept;
    private MockedStatic<SecurityUtils> mockedSecurtiyUtils;
    private static final String TESTUSER = "testUser";

    /**
     * Sets up a ProgressUpdaterConcept and opens the Mock
     */
    @BeforeEach
    void setUp() {
        progressUpdaterConcept = new ProgressUpdaterConcept(linkConcpetProgressRepository);
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
    void updateConceptProgressSuccess() {
        mockedSecurtiyUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkConcpetProgressRepository.getMaxQuestionsForRepo(TESTUSER)).thenReturn(30);
        when(linkConcpetProgressRepository.getCurCompentencyCP(TESTUSER)).thenReturn(0.2);

        progressUpdaterConcept.updateConceptProgress(3);

        verify(linkConcpetProgressRepository).updateConceptProgressToDBCP(
            competencyCaptor.capture(),
            improvementCaptor.capture(),
            nowCaptor.capture()
        );
        assertEquals(0.3f, competencyCaptor.getValue(), 0.001);
        assertEquals(0.1f, improvementCaptor.getValue(), 0.001);
        assertNotNull(nowCaptor.getValue());
    }

    /**
     * Tests to see if the compentency gets round up correctly
     */
    @Test
    void updateConceptProgressWith99() {
        mockedSecurtiyUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkConcpetProgressRepository.getMaxQuestionsForRepo(TESTUSER)).thenReturn(100);
        when(linkConcpetProgressRepository.getCurCompentencyCP(TESTUSER)).thenReturn(0.98);

        progressUpdaterConcept.updateConceptProgress(1);

        verify(linkConcpetProgressRepository).updateConceptProgressToDBCP(
            competencyCaptor.capture(),
            improvementCaptor.capture(),
            nowCaptor.capture()
        );
        assertEquals(1.0f, competencyCaptor.getValue(), 0.001);
        assertEquals(0.01f, improvementCaptor.getValue(), 0.0001);
        assertNotNull(nowCaptor.getValue());
    }

    /**
     * Tests to see if the compentency gets round up correctly
     */
    @Test
    void updateConceptProgressWithMoreThan99() {
        mockedSecurtiyUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkConcpetProgressRepository.getMaxQuestionsForRepo(TESTUSER)).thenReturn(100);
        when(linkConcpetProgressRepository.getCurCompentencyCP(TESTUSER)).thenReturn(0.98);

        progressUpdaterConcept.updateConceptProgress(15);

        verify(linkConcpetProgressRepository).updateConceptProgressToDBCP(
            competencyCaptor.capture(),
            improvementCaptor.capture(),
            nowCaptor.capture()
        );
        assertEquals(1.0f, competencyCaptor.getValue(), 0.001);
        assertEquals(0.0f, improvementCaptor.getValue(), 0.01);
        assertNotNull(nowCaptor.getValue());
    }

    /**
     * Tests to see if throws execption when no user is logged in
     */
    @Test
    void updateConceptProgressExceptionNoUser() {
        mockedSecurtiyUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> progressUpdaterConcept.updateConceptProgress(3));
        verifyNoInteractions(linkConcpetProgressRepository);
    }

    /**
     * Tests to see if throws execption when totalQuestions has a bad read
     */
    @Test
    void updateConceptProgressExceptionBadReadTotalQuestions() {
        mockedSecurtiyUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkConcpetProgressRepository.getMaxQuestionsForRepo(TESTUSER)).thenReturn(-1);

        assertThrows(IllegalArgumentException.class, () -> progressUpdaterConcept.updateConceptProgress(3));
    }

    /**
     * Tests to see if throws execption when currentComptency has a bad read
     */
    @Test
    void updateConceptProgressExceptionBadReadCurrentCompetency() {
        mockedSecurtiyUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkConcpetProgressRepository.getMaxQuestionsForRepo(TESTUSER)).thenReturn(100);
        when(linkConcpetProgressRepository.getCurCompentencyCP(TESTUSER)).thenReturn(-1.0);

        assertThrows(IllegalArgumentException.class, () -> progressUpdaterConcept.updateConceptProgress(3));
    }

    /**
     * Tests to see if the getImprovement function works
     */
    @Test
    void getImprovementSuccess() {
        mockedSecurtiyUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkConcpetProgressRepository.getImprovementCP(TESTUSER)).thenReturn(7.2);

        float improvementTest = progressUpdaterConcept.getImprovement();

        assertEquals(7.2f, improvementTest);
    }

    /**
     * Tests to see if throws execption when improvemnt has a bad read
     */
    @Test
    void getImprovementExeceptionBadReadImprovement() {
        mockedSecurtiyUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkConcpetProgressRepository.getImprovementCP(TESTUSER)).thenReturn(-1.0);

        assertThrows(IllegalArgumentException.class, () -> progressUpdaterConcept.getImprovement());
    }
}
