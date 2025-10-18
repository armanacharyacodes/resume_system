package com.resume_system.resume_system.service;

import com.resume_system.resume_system.dto.ResumeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface ResumeService {

    ResumeDTO create(ResumeDTO dto, Long userId);

    ResumeDTO getById(Long id);

    Page<ResumeDTO> listByUser(Long userId, Pageable pageable);

    Page<ResumeDTO> filterBySkill(String skill, Pageable pageable);

    @Transactional
    ResumeDTO update(Long id, ResumeDTO dto, Long userId);

    @Transactional
    void delete(Long id, Long userId);
}
