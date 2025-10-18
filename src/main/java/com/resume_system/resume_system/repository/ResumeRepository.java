package com.resume_system.resume_system.repository;

import com.resume_system.resume_system.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Page<Resume> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT r FROM Resume r JOIN r.skills s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :skill, '%'))")
    Page<Resume> findBySkillContaining(@Param("skill") String skill, Pageable pageable);
}