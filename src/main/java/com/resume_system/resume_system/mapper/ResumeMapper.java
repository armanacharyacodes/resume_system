package com.resume_system.resume_system.mapper;

import com.resume_system.resume_system.dto.AchievementDTO;
import com.resume_system.resume_system.dto.CourseDTO;
import com.resume_system.resume_system.dto.ProjectDTO;
import com.resume_system.resume_system.dto.ResumeDTO;
import com.resume_system.resume_system.dto.SkillsDTO;
import com.resume_system.resume_system.entity.Achievement;
import com.resume_system.resume_system.entity.Course;
import com.resume_system.resume_system.entity.Project;
import com.resume_system.resume_system.entity.Resume;
import com.resume_system.resume_system.entity.Skills;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ResumeMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "achievements", expression = "java(mapAchievements(resume.getAchievements()))")
    @Mapping(target = "courses", expression = "java(mapCourses(resume.getCourses()))")
    @Mapping(target = "projects", expression = "java(mapProjects(resume.getProjects()))")
    @Mapping(target = "skills", expression = "java(mapSkills(resume.getSkills()))")
    ResumeDTO toDto(Resume resume);

    AchievementDTO toDto(Achievement achievement);

    CourseDTO toDto(Course course);

    ProjectDTO toDto(Project project);

    SkillsDTO toDto(Skills skill);

    @Mapping(target = "user", ignore = true)
    Resume toEntity(ResumeDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(ResumeDTO dto, @MappingTarget Resume entity);

    default List<AchievementDTO> mapAchievements(List<Achievement> achievements) {
        return achievements == null ? List.of() : achievements.stream().map(this::toDto).collect(Collectors.toList());
    }

    default List<CourseDTO> mapCourses(List<Course> courses) {
        return courses == null ? List.of() : courses.stream().map(this::toDto).collect(Collectors.toList());
    }

    default List<ProjectDTO> mapProjects(List<Project> projects) {
        return projects == null ? List.of() : projects.stream().map(this::toDto).collect(Collectors.toList());
    }

    default List<SkillsDTO> mapSkills(List<Skills> skills) {
        return skills == null ? List.of() : skills.stream().map(this::toDto).collect(Collectors.toList());
    }
}
