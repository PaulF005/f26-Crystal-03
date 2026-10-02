package com.crystal.kip.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface LinkScenarioRepository extends ScenarioRepository, ACustomScenarioRepository {
    //Left intentionally blank, use this for code
}
