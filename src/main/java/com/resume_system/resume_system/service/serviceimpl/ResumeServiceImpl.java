package com.resume_system.resume_system.service.serviceimpl;

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
import com.resume_system.resume_system.entity.User;
import com.resume_system.resume_system.exception.ResumeNotFoundException;
import com.resume_system.resume_system.exception.ResumeOwnershipException;
import com.resume_system.resume_system.mapper.ResumeMapper;
import com.resume_system.resume_system.repository.AchievementRepository;
import com.resume_system.resume_system.repository.CourseRepository;
import com.resume_system.resume_system.repository.ProjectRepository;
import com.resume_system.resume_system.repository.ResumeRepository;
import com.resume_system.resume_system.repository.SkillRepository;
import com.resume_system.resume_system.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeMapper resumeMapper;
    private final AchievementRepository achievementRepository;
    private final CourseRepository courseRepository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;

    @Override
    @Transactional
    public ResumeDTO create(ResumeDTO dto, Long userId) {
        Resume resume = Resume.builder()
                .title(dto.getTitle())
                .summary(dto.getSummary())
                .user(User.builder().id(userId).build())
                .build();
        resume = resumeRepository.save(resume);

        saveChildren(resume, dto);

        return resumeMapper.toDto(resume);
    }

    @Override
    public ResumeDTO getById(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResumeNotFoundException("Resume not found"));
        return resumeMapper.toDto(resume);
    }

    @Override
    public Page<ResumeDTO> listByUser(Long userId, Pageable pageable) {
        return resumeRepository.findByUserId(userId, pageable)
                .map(resumeMapper::toDto);
    }

    @Override
    public Page<ResumeDTO> filterBySkill(String skill, Pageable pageable) {
        return resumeRepository.findBySkillContaining(skill, pageable)
                .map(resumeMapper::toDto);
    }

    @Override
    @Transactional
    public ResumeDTO update(Long id, ResumeDTO dto, Long userId) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResumeNotFoundException("Resume not found"));

        if (!resume.getUser().getId().equals(userId)) {
            throw new ResumeOwnershipException("You do not have permission to update this resume");
        }

        resume.setTitle(dto.getTitle());
        resume.setSummary(dto.getSummary());

        updateChildren(resume, dto);

        resume = resumeRepository.save(resume);
        return resumeMapper.toDto(resume);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResumeNotFoundException("Resume not found"));

        if (!resume.getUser().getId().equals(userId)) {
            throw new ResumeOwnershipException("You do not have permission to delete this resume");
        }

        resumeRepository.delete(resume);
    }

    private void saveChildren(Resume resume, ResumeDTO dto) {
        if (dto.getAchievements() != null) dto.getAchievements().forEach(a -> saveAchievement(a, resume));
        if (dto.getCourses() != null) dto.getCourses().forEach(c -> saveCourse(c, resume));
        if (dto.getProjects() != null) dto.getProjects().forEach(p -> saveProject(p, resume));
        if (dto.getSkills() != null) dto.getSkills().forEach(s -> saveSkill(s, resume));
    }

    private void updateChildren(Resume resume, ResumeDTO dto) {
        Map<Long, Achievement> currentAchievements = new HashMap<>();
        resume.getAchievements().forEach(a -> currentAchievements.put(a.getId(), a));
        List<Achievement> updatedAchievements = new ArrayList<>();
        if (dto.getAchievements() != null) {
            for (AchievementDTO aDto : dto.getAchievements()) {
                Achievement a;
                if (aDto.getId() != null && currentAchievements.containsKey(aDto.getId())) {
                    a = currentAchievements.get(aDto.getId());
                    a.setTitle(aDto.getTitle());
                    a.setDescription(aDto.getDescription());
                } else {
                    a = Achievement.builder()
                            .title(aDto.getTitle())
                            .description(aDto.getDescription())
                            .resume(resume)
                            .build();
                }
                updatedAchievements.add(a);
                achievementRepository.save(a);
            }
        }
        resume.getAchievements().clear();
        resume.getAchievements().addAll(updatedAchievements);

        Map<Long, Course> currentCourses = new HashMap<>();
        resume.getCourses().forEach(c -> currentCourses.put(c.getId(), c));
        List<Course> updatedCourses = new ArrayList<>();
        if (dto.getCourses() != null) {
            for (CourseDTO cDto : dto.getCourses()) {
                Course c;
                if (cDto.getId() != null && currentCourses.containsKey(cDto.getId())) {
                    c = currentCourses.get(cDto.getId());
                    c.setName(cDto.getName());
                    c.setProvider(cDto.getProvider());
                    c.setCompletedDate(cDto.getCompletedDate());
                } else {
                    c = Course.builder()
                            .name(cDto.getName())
                            .provider(cDto.getProvider())
                            .completedDate(cDto.getCompletedDate())
                            .resume(resume)
                            .build();
                }
                updatedCourses.add(c);
                courseRepository.save(c);
            }
        }
        resume.getCourses().clear();
        resume.getCourses().addAll(updatedCourses);

        Map<Long, Project> currentProjects = new HashMap<>();
        resume.getProjects().forEach(p -> currentProjects.put(p.getId(), p));
        List<Project> updatedProjects = new ArrayList<>();
        if (dto.getProjects() != null) {
            for (ProjectDTO pDto : dto.getProjects()) {
                Project p;
                if (pDto.getId() != null && currentProjects.containsKey(pDto.getId())) {
                    p = currentProjects.get(pDto.getId());
                    p.setName(pDto.getName());
                    p.setDescription(pDto.getDescription());
                    p.setUrl(pDto.getUrl());
                } else {
                    p = Project.builder()
                            .name(pDto.getName())
                            .description(pDto.getDescription())
                            .url(pDto.getUrl())
                            .resume(resume)
                            .build();
                }
                updatedProjects.add(p);
                projectRepository.save(p);
            }
        }
        resume.getProjects().clear();
        resume.getProjects().addAll(updatedProjects);

        Map<Long, Skills> currentSkills = new HashMap<>();
        resume.getSkills().forEach(s -> currentSkills.put(s.getId(), s));
        List<Skills> updatedSkills = new ArrayList<>();
        if (dto.getSkills() != null) {
            for (SkillsDTO sDto : dto.getSkills()) {
                Skills s;
                if (sDto.getId() != null && currentSkills.containsKey(sDto.getId())) {
                    s = currentSkills.get(sDto.getId());
                    s.setName(sDto.getName());
                    s.setLevel(sDto.getLevel());
                } else {
                    s = Skills.builder()
                            .name(sDto.getName())
                            .level(sDto.getLevel())
                            .resume(resume)
                            .build();
                }
                updatedSkills.add(s);
                skillRepository.save(s);
            }
        }
        resume.getSkills().clear();
        resume.getSkills().addAll(updatedSkills);
    }

    private void saveAchievement(AchievementDTO dto, Resume resume) {
        achievementRepository.save(
                Achievement.builder()
                        .title(dto.getTitle())
                        .description(dto.getDescription())
                        .resume(resume)
                        .build()
        );
    }

    private void saveCourse(CourseDTO dto, Resume resume) {
        courseRepository.save(
                Course.builder()
                        .name(dto.getName())
                        .provider(dto.getProvider())
                        .completedDate(dto.getCompletedDate())
                        .resume(resume)
                        .build()
        );
    }

    private void saveProject(ProjectDTO dto, Resume resume) {
        projectRepository.save(
                Project.builder()
                        .name(dto.getName())
                        .description(dto.getDescription())
                        .url(dto.getUrl())
                        .resume(resume)
                        .build()
        );
    }

    private void saveSkill(SkillsDTO dto, Resume resume) {
        skillRepository.save(
                Skills.builder()
                        .name(dto.getName())
                        .level(dto.getLevel())
                        .resume(resume)
                        .build()
        );
    }
}
