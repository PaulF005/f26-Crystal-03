package com.crystal.kip.repository.LinkRepo;

import com.crystal.kip.repository.ACustomRepoCode.ACustomScenarioRepository;
import com.crystal.kip.repository.ScenarioRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface LinkScenarioRepository extends ScenarioRepository, ACustomScenarioRepository {
    //Left intentionally blank, use this in application code
}
