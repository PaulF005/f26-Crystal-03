package com.crystal.kip.repository.LinkRepo;

import com.crystal.kip.repository.ConceptProgressRepository;
import com.crystal.kip.repository.CustomRepoCode.CustomConceptProgressRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface LinkConceptProgressRepository extends ConceptProgressRepository, CustomConceptProgressRepository {
    //Left intentionally blank, use this in application code
}
