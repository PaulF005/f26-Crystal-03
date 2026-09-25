package com.crystal.kip.progress_update;

import com.crystal.kip.repository.ConceptProgressRepository;
import com.crystal.kip.repository.TopicProgressRepository;
import org.springframework.stereotype.Service;

@Service
public class ProgressUpdaterTopic {

    private final ConceptProgressRepository conceptProgressRepository;

    public ProgressUpdaterTopic(ConceptProgressRepository conceptProgressRepository) {
        this.conceptProgressRepository = conceptProgressRepository;
    }
}
