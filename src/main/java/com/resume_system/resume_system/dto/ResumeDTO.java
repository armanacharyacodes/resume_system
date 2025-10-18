package com.resume_system.resume_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDTO {
    private Long id;
    private Long userId;
    private String title;
    private String summary;
    private List<AchievementDTO> achievements;
    private List<CourseDTO> courses;
    private List<ProjectDTO> projects;
    private List<SkillsDTO> skills;
}