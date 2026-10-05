package com.crystal.kip.repository.LinkRepo;

import com.crystal.kip.repository.ACustomRepoCode.ACustomTopicProgressRepository;
import com.crystal.kip.repository.TopicProgressRepository;
import java.time.Instant;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface LinkTopicProgressRepository extends TopicProgressRepository, ACustomTopicProgressRepository {
    //Left intentionally blank, use this in application code
}
