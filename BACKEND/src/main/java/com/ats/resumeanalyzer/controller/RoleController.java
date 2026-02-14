package com.ats.resumeanalyzer.controller;

import com.ats.resumeanalyzer.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class RoleController {

    @Autowired
    private ResumeService resumeService;

    @GetMapping("/api/roles")
    public List<String> getRoles() {
        return resumeService.getAllRolesList();
    }
}
