package com.ats.resumeanalyzer.controller;

import com.ats.resumeanalyzer.dto.ResumeResponseDTO;
import com.ats.resumeanalyzer.service.ResumeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "*")   // ✅ Allow all origins (important for deployed frontend)
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    // ================= RESUME UPLOAD =================
    @PostMapping("/upload")
    public ResponseEntity<ResumeResponseDTO> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("role") String role,
            @RequestParam("experience") int experience
    ) {
        return ResponseEntity.ok(
                resumeService.analyzeResumeFile(file, role, experience)
        );
    }

    // ================= GET ALL ROLES (FOR FRONTEND DROPDOWN) =================
    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoles() {
        return ResponseEntity.ok(resumeService.getAllRolesList());
    }
}
