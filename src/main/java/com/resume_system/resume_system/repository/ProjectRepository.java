package com.resume_system.resume_system.repository;

import com.resume_system.resume_system.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}