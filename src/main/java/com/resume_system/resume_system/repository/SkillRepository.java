package com.resume_system.resume_system.repository;

import com.resume_system.resume_system.entity.Skills;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skills, Long> {
}