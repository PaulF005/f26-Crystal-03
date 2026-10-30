package com.crystal.kip.progress_update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.crystal.kip.repository.LinkRepo.LinkTopicProgressRepository;
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
public class ProgressUpdaterTopicTest {

    @Mock
    private LinkTopicProgressRepository linkTopicProgressRepository;

    @Captor
    private ArgumentCaptor<Float> competencyCaptor;

    @Captor
    private ArgumentCaptor<Float> improvementCaptor;

    @Captor
    private ArgumentCaptor<Instant> nowCaptor;

    private ProgressUpdaterTopic progressUpdaterTopic;
    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    private static final String TESTUSER = "testUser";
    private static final Long TOPIC_ID = 23L;

    @BeforeEach
    void setUp() {
        progressUpdaterTopic = new ProgressUpdaterTopic(linkTopicProgressRepository);
        mockedSecurityUtils = mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedSecurityUtils != null) {
            mockedSecurityUtils.close();
        }
    }

    @Test
    void updateTopicProgressSuccess() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkTopicProgressRepository.getCurCompetencyTP(TESTUSER, TOPIC_ID)).thenReturn(0.2);
        when(linkTopicProgressRepository.getAllTopicNumTP(TOPIC_ID)).thenReturn(2);

        progressUpdaterTopic.updateTopicProgress(TOPIC_ID, 0.2f);

        verify(linkTopicProgressRepository).getCurCompetencyTP(TESTUSER, TOPIC_ID);
        verify(linkTopicProgressRepository).getAllTopicNumTP(TOPIC_ID);
        verify(linkTopicProgressRepository).updateTopicProgressToDB(
            eq(TOPIC_ID),
            competencyCaptor.capture(),
            improvementCaptor.capture(),
            nowCaptor.capture()
        );

        assertEquals(0.3f, competencyCaptor.getValue(), 0.001);
        assertEquals(0.1f, improvementCaptor.getValue(), 0.001);
        assertNotNull(nowCaptor.getValue());
    }

    @Test
    void updateTopicProgressWith99() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkTopicProgressRepository.getCurCompetencyTP(TESTUSER, TOPIC_ID)).thenReturn(0.98);
        when(linkTopicProgressRepository.getAllTopicNumTP(TOPIC_ID)).thenReturn(2);

        progressUpdaterTopic.updateTopicProgress(TOPIC_ID, 0.02f);

        verify(linkTopicProgressRepository).updateTopicProgressToDB(
            eq(TOPIC_ID),
            competencyCaptor.capture(),
            improvementCaptor.capture(),
            nowCaptor.capture()
        );

        assertEquals(1.0f, competencyCaptor.getValue(), 0.001);
        assertEquals(0.01f, improvementCaptor.getValue(), 0.0001);
        assertNotNull(nowCaptor.getValue());
    }

    @Test
    void updateTopicProgressWithMoreThan99() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkTopicProgressRepository.getCurCompetencyTP(TESTUSER, TOPIC_ID)).thenReturn(0.98);
        when(linkTopicProgressRepository.getAllTopicNumTP(TOPIC_ID)).thenReturn(2);

        progressUpdaterTopic.updateTopicProgress(TOPIC_ID, 0.15f);

        verify(linkTopicProgressRepository).updateTopicProgressToDB(
            eq(TOPIC_ID),
            competencyCaptor.capture(),
            improvementCaptor.capture(),
            nowCaptor.capture()
        );

        assertEquals(1.0f, competencyCaptor.getValue(), 0.001);
        assertEquals(0.0f, improvementCaptor.getValue(), 0.01);
        assertNotNull(nowCaptor.getValue());
    }

    @Test
    void updateTopicProgressExceptionNoUser() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> progressUpdaterTopic.updateTopicProgress(TOPIC_ID, 0.3f));

        verifyNoInteractions(linkTopicProgressRepository);
    }

    @Test
    void updateTopicProgressExceptionBadReadConceptCount() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkTopicProgressRepository.getCurCompetencyTP(TESTUSER, TOPIC_ID)).thenReturn(0.2);
        when(linkTopicProgressRepository.getAllTopicNumTP(TOPIC_ID)).thenReturn(-1);

        assertThrows(IllegalArgumentException.class, () -> progressUpdaterTopic.updateTopicProgress(TOPIC_ID, 0.3f));
    }

    @Test
    void updateTopicProgressExceptionZeroConcepts() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkTopicProgressRepository.getCurCompetencyTP(TESTUSER, TOPIC_ID)).thenReturn(0.2);
        when(linkTopicProgressRepository.getAllTopicNumTP(TOPIC_ID)).thenReturn(0);

        assertThrows(IllegalArgumentException.class, () -> progressUpdaterTopic.updateTopicProgress(TOPIC_ID, 0.3f));
    }

    @Test
    void updateTopicProgressExceptionBadReadCurrentCompetency() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(TESTUSER));
        when(linkTopicProgressRepository.getCurCompetencyTP(TESTUSER, TOPIC_ID)).thenReturn(-1.0);

        assertThrows(IllegalArgumentException.class, () -> progressUpdaterTopic.updateTopicProgress(TOPIC_ID, 0.3f));
    }
}
