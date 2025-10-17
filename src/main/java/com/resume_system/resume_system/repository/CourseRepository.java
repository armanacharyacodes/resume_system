package com.resume_system.resume_system.repository;

import com.resume_system.resume_system.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}