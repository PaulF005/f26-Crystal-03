package com.crystal.kip.repository;

import com.crystal.kip.domain.GameProgress;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the GameProgress entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GameProgressRepository extends JpaRepository<GameProgress, Long> {
    List<GameProgress> findByUserProfile_DataUser_Login(String login);
}
