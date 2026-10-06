package com.crystal.kip.repository.LinkRepo;

import com.crystal.kip.repository.ACustomRepoCode.ACustomConceptProgressRepository;
import com.crystal.kip.repository.ConceptProgressRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface LinkConcpetProgressRepository extends ConceptProgressRepository, ACustomConceptProgressRepository {
    //Left intentionally blank, use this in application code
}
