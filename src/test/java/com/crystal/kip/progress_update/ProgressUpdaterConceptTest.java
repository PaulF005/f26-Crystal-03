package com.crystal.kip.progress_update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.crystal.kip.repository.LinkRepo.LinkConceptProgressRepository;
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

    @Mock
    private LinkConceptProgressRepository linkConceptProgressRepository;

    @Captor
    private ArgumentCaptor<Float> competencyCaptor;

    @Captor
    private ArgumentCaptor<Float> improvementCaptor;

    @Captor
    private ArgumentCaptor<Instant> nowCaptor;

    private ProgressUpdaterConcept progressUpdaterConcept;
    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    private static final String TESTUSER = "testUser";
    private static final Long CONCEPT_ID = 17L;

    @BeforeEach
    void setUp() {
        progressUpdaterConcept = new ProgressUpdaterConcept(linkConceptProgressRepository);
        mockedSecurityUtils = mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedSecurityUtils != null) {
            mockedSecurityUtils.close();
        }
    }

    @Test
    void updateConceptProgressSuccess() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkConceptProgressRepository.getMaxQuestionsForRepo(TESTUSER, CONCEPT_ID)).thenReturn(30);
        when(linkConceptProgressRepository.getCurCompetencyCP(TESTUSER, CONCEPT_ID)).thenReturn(0.2);

        float improvement = progressUpdaterConcept.updateConceptProgress(CONCEPT_ID, 3);

        verify(linkConceptProgressRepository).getMaxQuestionsForRepo(TESTUSER, CONCEPT_ID);
        verify(linkConceptProgressRepository).getCurCompetencyCP(TESTUSER, CONCEPT_ID);
        verify(linkConceptProgressRepository).updateConceptProgressToDBCP(
            eq(CONCEPT_ID),
            competencyCaptor.capture(),
            improvementCaptor.capture(),
            nowCaptor.capture()
        );

        assertEquals(0.3f, competencyCaptor.getValue(), 0.001);
        assertEquals(0.1f, improvementCaptor.getValue(), 0.001);
        assertEquals(0.1f, improvement, 0.001);
        assertNotNull(nowCaptor.getValue());
    }

    @Test
    void updateConceptProgressDoesNotRound99ToOne() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkConceptProgressRepository.getMaxQuestionsForRepo(TESTUSER, CONCEPT_ID)).thenReturn(100);
        when(linkConceptProgressRepository.getCurCompetencyCP(TESTUSER, CONCEPT_ID)).thenReturn(0.98);

        float improvement = progressUpdaterConcept.updateConceptProgress(CONCEPT_ID, 1);

        verify(linkConceptProgressRepository).getMaxQuestionsForRepo(TESTUSER, CONCEPT_ID);
        verify(linkConceptProgressRepository).getCurCompetencyCP(TESTUSER, CONCEPT_ID);
        verify(linkConceptProgressRepository).updateConceptProgressToDBCP(
            eq(CONCEPT_ID),
            competencyCaptor.capture(),
            improvementCaptor.capture(),
            nowCaptor.capture()
        );

        assertEquals(0.99f, competencyCaptor.getValue(), 0.001);
        assertEquals(0.01f, improvementCaptor.getValue(), 0.0001);
        assertEquals(0.01f, improvement, 0.0001);
        assertNotNull(nowCaptor.getValue());
    }

    @Test
    void updateConceptProgressClampsAtOneAndReturnsActualImprovement() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkConceptProgressRepository.getMaxQuestionsForRepo(TESTUSER, CONCEPT_ID)).thenReturn(100);
        when(linkConceptProgressRepository.getCurCompetencyCP(TESTUSER, CONCEPT_ID)).thenReturn(0.98);

        float improvement = progressUpdaterConcept.updateConceptProgress(CONCEPT_ID, 15);

        verify(linkConceptProgressRepository).getMaxQuestionsForRepo(TESTUSER, CONCEPT_ID);
        verify(linkConceptProgressRepository).getCurCompetencyCP(TESTUSER, CONCEPT_ID);
        verify(linkConceptProgressRepository).updateConceptProgressToDBCP(
            eq(CONCEPT_ID),
            competencyCaptor.capture(),
            improvementCaptor.capture(),
            nowCaptor.capture()
        );

        assertEquals(1.0f, competencyCaptor.getValue(), 0.001);
        assertEquals(0.02f, improvementCaptor.getValue(), 0.001);
        assertEquals(0.02f, improvement, 0.001);
        assertNotNull(nowCaptor.getValue());
    }

    @Test
    void updateConceptProgressAtMaxReturnsNoImprovement() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkConceptProgressRepository.getMaxQuestionsForRepo(TESTUSER, CONCEPT_ID)).thenReturn(100);
        when(linkConceptProgressRepository.getCurCompetencyCP(TESTUSER, CONCEPT_ID)).thenReturn(1.0);

        float improvement = progressUpdaterConcept.updateConceptProgress(CONCEPT_ID, 10);

        verify(linkConceptProgressRepository).updateConceptProgressToDBCP(
            eq(CONCEPT_ID),
            competencyCaptor.capture(),
            improvementCaptor.capture(),
            nowCaptor.capture()
        );

        assertEquals(1.0f, competencyCaptor.getValue(), 0.001);
        assertEquals(0.0f, improvementCaptor.getValue(), 0.001);
        assertEquals(0.0f, improvement, 0.001);
    }

    @Test
    void updateConceptProgressExceptionNoUser() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> progressUpdaterConcept.updateConceptProgress(CONCEPT_ID, 3));

        verifyNoInteractions(linkConceptProgressRepository);
    }

    @Test
    void updateConceptProgressExceptionBadReadTotalQuestions() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkConceptProgressRepository.getMaxQuestionsForRepo(TESTUSER, CONCEPT_ID)).thenReturn(-1);

        assertThrows(IllegalArgumentException.class, () -> progressUpdaterConcept.updateConceptProgress(CONCEPT_ID, 3));
    }

    @Test
    void updateConceptProgressExceptionZeroQuestions() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkConceptProgressRepository.getMaxQuestionsForRepo(TESTUSER, CONCEPT_ID)).thenReturn(0);

        assertThrows(IllegalArgumentException.class, () -> progressUpdaterConcept.updateConceptProgress(CONCEPT_ID, 3));
    }

    @Test
    void updateConceptProgressExceptionBadReadCurrentCompetency() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkConceptProgressRepository.getMaxQuestionsForRepo(TESTUSER, CONCEPT_ID)).thenReturn(100);
        when(linkConceptProgressRepository.getCurCompetencyCP(TESTUSER, CONCEPT_ID)).thenReturn(-1.0);

        assertThrows(IllegalArgumentException.class, () -> progressUpdaterConcept.updateConceptProgress(CONCEPT_ID, 3));
    }

    @Test
    void updateConceptProgressUsesRequestedConceptId() {
        Long otherConceptId = 99L;

        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkConceptProgressRepository.getMaxQuestionsForRepo(TESTUSER, otherConceptId)).thenReturn(10);
        when(linkConceptProgressRepository.getCurCompetencyCP(TESTUSER, otherConceptId)).thenReturn(0.4);

        float improvement = progressUpdaterConcept.updateConceptProgress(otherConceptId, 2);

        verify(linkConceptProgressRepository).getMaxQuestionsForRepo(TESTUSER, otherConceptId);
        verify(linkConceptProgressRepository).getCurCompetencyCP(TESTUSER, otherConceptId);
        verify(linkConceptProgressRepository).updateConceptProgressToDBCP(
            eq(otherConceptId),
            competencyCaptor.capture(),
            improvementCaptor.capture(),
            nowCaptor.capture()
        );

        assertEquals(0.6f, competencyCaptor.getValue(), 0.001);
        assertEquals(0.2f, improvementCaptor.getValue(), 0.001);
        assertEquals(0.2f, improvement, 0.001);
    }
}
