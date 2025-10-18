package com.resume_system.resume_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillsDTO {
    private Long id;
    private String name;
    private String level; // BEGINNER, INTERMEDIATE, ADVANCED
    private Long resumeId;
}