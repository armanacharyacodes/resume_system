package com.resume_system.resume_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume_system.resume_system.dto.AchievementDTO;
import com.resume_system.resume_system.dto.CourseDTO;
import com.resume_system.resume_system.dto.ProjectDTO;
import com.resume_system.resume_system.dto.ResumeDTO;
import com.resume_system.resume_system.dto.SkillsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ResumeControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("test")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void createAndFetchResume() throws Exception {
        ResumeDTO dto = ResumeDTO.builder()
                .title("Final Resume")
                .summary("Complete version")
                .achievements(List.of(
                        AchievementDTO.builder().title("Winner").description("2025").build()
                ))
                .courses(List.of(
                        CourseDTO.builder().name("Spring Boot").provider("Udemy").completedDate(LocalDate.now()).build()
                ))
                .projects(List.of(
                        ProjectDTO.builder().name("Resume App").description("Spring project").url("http://example.com").build()
                ))
                .skills(List.of(
                        SkillsDTO.builder().name("Java").level("ADVANCED").build()
                ))
                .build();

        String json = mvc.perform(post("/api/resumes")
                        .header("Authorization", "Bearer " + obtainToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Final Resume"))
                .andExpect(jsonPath("$.achievements[0].title").value("Winner"))
                .andExpect(jsonPath("$.courses[0].name").value("Spring Boot"))
                .andExpect(jsonPath("$.projects[0].name").value("Resume App"))
                .andExpect(jsonPath("$.skills[0].name").value("Java"))
                .andReturn().getResponse().getContentAsString();

        ResumeDTO created = mapper.readValue(json, ResumeDTO.class);

        mvc.perform(get("/api/resumes/" + created.getId())
                        .header("Authorization", "Bearer " + obtainToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.title").value("Final Resume"));
    }

    private String obtainToken() throws Exception {
        String login = """
                {"email":"user1@gmail.com","password":"user$1234"}
                """;
        return mapper.readTree(
                        mvc.perform(post("/api/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(login))
                                .andReturn()
                                .getResponse()
                                .getContentAsString())
                .get("accessToken").asText();
    }
}
