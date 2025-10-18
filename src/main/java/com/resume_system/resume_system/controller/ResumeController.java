package com.resume_system.resume_system.controller;

import com.resume_system.resume_system.dto.ResumeDTO;
import com.resume_system.resume_system.entity.User;
import com.resume_system.resume_system.exception.ResumeOwnershipException;
import com.resume_system.resume_system.repository.UserRepository;
import com.resume_system.resume_system.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
@Tag(name = "Resume", description = "Resume CRUD endpoints")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserRepository userRepository;

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create new resume", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResumeDTO> create(@RequestBody ResumeDTO dto, Principal principal) {
        Long userId = getUserId(principal);
        ResumeDTO created = resumeService.create(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get single resume", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResumeDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(resumeService.getById(id));
    }

    @GetMapping("/listMyResumes")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List my resumes (pageable)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Page<ResumeDTO>> listMyResumes(@PageableDefault(size = 10) Pageable pageable,
                                                         Principal principal) {
        Long userId = getUserId(principal);
        return ResponseEntity.ok(resumeService.listByUser(userId, pageable));
    }

    @GetMapping("/filter")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Filter resumes by skill (pageable)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Page<ResumeDTO>> filterBySkill(@RequestParam String skill,
                                                         @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(resumeService.filterBySkill(skill, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update resume", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResumeDTO> update(@PathVariable Long id,
                                            @RequestBody ResumeDTO dto,
                                            Principal principal) {
        Long userId = getUserId(principal);
        ResumeDTO updated = resumeService.update(id, dto, userId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete resume", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        Long userId = getUserId(principal);
        resumeService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    /* Helper: safely get userId from Principal */
    private Long getUserId(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new ResumeOwnershipException("User not authenticated");
        }
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResumeOwnershipException("User not found"));
        return user.getId();
    }
}
