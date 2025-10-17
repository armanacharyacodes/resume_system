package com.resume_system.resume_system.repository;

import com.resume_system.resume_system.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
}