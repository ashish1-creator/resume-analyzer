package com.ats.resumeanalyzer.service;

import com.ats.resumeanalyzer.dto.ResumeResponseDTO;
import com.ats.resumeanalyzer.exception.ApiException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResumeService {

    private static final Map<String, List<String>> COURSE_SKILLS = new LinkedHashMap<>();
    private static final Map<String, String> ROLE_ALIASES = new HashMap<>();

    static {

        COURSE_SKILLS.put("Data Analyst", List.of(
                "sql", "advanced sql", "joins", "subqueries",
                "excel", "vlookup", "pivot tables",
                "power bi", "dax",
                "tableau", "data visualization",
                "python", "pandas", "numpy",
                "statistics", "business analysis",
                "data cleaning", "reporting"
        ));

        COURSE_SKILLS.put("Data Scientist", List.of(
                "python", "pandas", "numpy",
                "statistics", "probability",
                "machine learning", "supervised learning", "unsupervised learning",
                "feature engineering",
                "scikit-learn",
                "data visualization", "matplotlib", "seaborn",
                "model evaluation",
                "sql", "data wrangling"
        ));

        COURSE_SKILLS.put("Cyber Security", List.of(
                "networking", "tcp ip",
                "linux", "bash",
                "cyber security fundamentals",
                "penetration testing",
                "ethical hacking",
                "vulnerability assessment",
                "firewalls",
                "wireshark",
                "owasp",
                "incident response",
                "risk management"
        ));

        COURSE_SKILLS.put("Java Developer", List.of(
                "java", "oops", "collections", "exception handling",
                "multithreading",
                "spring", "spring boot",
                "hibernate", "jpa",
                "rest api",
                "sql", "jdbc",
                "maven", "git",
                "unit testing", "junit",
                "design patterns"
        ));

        COURSE_SKILLS.put("Frontend Developer", List.of(
                "html", "semantic html",
                "css", "flexbox", "grid",
                "javascript", "es6",
                "dom manipulation",
                "react", "hooks", "state management",
                "api integration",
                "responsive design",
                "bootstrap", "tailwind",
                "browser debugging",
                "git"
        ));

        COURSE_SKILLS.put("Python Developer", List.of(
                "python", "oops",
                "django", "flask",
                "rest api",
                "sql",
                "orm",
                "data structures",
                "exception handling",
                "unit testing",
                "git"
        ));

        COURSE_SKILLS.put("Python Full Stack Developer", List.of(
                "python",
                "django", "flask",
                "rest api",
                "html", "css", "javascript",
                "react",
                "sql",
                "authentication",
                "deployment",
                "git"
        ));

        COURSE_SKILLS.put("Automation Tester", List.of(
                "software testing fundamentals",
                "selenium",
                "java",
                "testng",
                "cucumber",
                "bdd framework",
                "test case design",
                "regression testing",
                "api testing",
                "manual testing basics",
                "git",
                "jenkins"
        ));

        COURSE_SKILLS.put("SQL Developer", List.of(
                "sql", "advanced sql",
                "joins", "indexes",
                "subqueries",
                "plsql",
                "procedures", "functions",
                "performance tuning",
                "query optimization",
                "database design",
                "normalization"
        ));

        COURSE_SKILLS.put("Machine Learning Engineer", List.of(
                "python",
                "machine learning",
                "statistics",
                "data preprocessing",
                "feature engineering",
                "model training",
                "tensorflow",
                "pytorch",
                "deep learning",
                "model evaluation",
                "deployment basics",
                "ml pipelines"
        ));

        COURSE_SKILLS.put("Full Stack Developer", List.of(
                "java",
                "spring boot",
                "rest api",
                "html", "css", "javascript",
                "react",
                "sql",
                "authentication",
                "microservices basics",
                "git",
                "deployment"
        ));

        COURSE_SKILLS.put("Java Full Stack Developer", List.of(
                "java", "oops",
                "spring boot",
                "hibernate", "jpa",
                "rest api",
                "html", "css", "javascript",
                "react",
                "sql",
                "authentication",
                "microservices basics",
                "git",
                "docker basics"
        ));

        COURSE_SKILLS.put("Mern Full Stack Developer", List.of(
                "html", "css", "javascript",
                "react", "hooks",
                "node",
                "express",
                "mongodb",
                "rest api",
                "jwt authentication",
                "state management",
                "git", "github",
                "deployment",
                "performance optimization"
        ));


        for (String role : COURSE_SKILLS.keySet()) {
            ROLE_ALIASES.put(normalizeRoleKey(role), role);
        }
    }

    public ResumeResponseDTO analyzeResumeFile(
            MultipartFile file,
            String role,
            int experience
    )
    {

        if (file == null || file.isEmpty()) {
            throw new ApiException("Resume file is required");
        }


        String canonicalRole = resolveRole(role);

        try {
            String extractedText = extractText(file);
            String normalizedText = normalizeText(extractedText);
            return analyzeText(canonicalRole, normalizedText);
        } catch (Exception e) {
            throw new ApiException("Failed to analyze resume");
        }
    }

    private ResumeResponseDTO analyzeText(String role, String text) {

        List<String> requiredSkills = COURSE_SKILLS.get(role);
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String skill : requiredSkills) {
            if (text.contains(skill)) {
                matchedSkills.add(skill);
            } else {
                missingSkills.add(skill);
            }
        }

        int score = (int) ((matchedSkills.size() * 100.0) / requiredSkills.size());

        String recommendation =
                score >= 80 ? "Excellent fit for " + role :
                        score >= 50 ? "Good fit, improve missing skills" :
                                "Needs more skill development";

        return new ResumeResponseDTO(
                role, score, matchedSkills, missingSkills, recommendation
        );
    }

    private String resolveRole(String inputRole) {

        if (inputRole == null || inputRole.trim().isEmpty()) {
            throw new ApiException("Role is required");
        }

        String key = normalizeRoleKey(inputRole);

        if (!ROLE_ALIASES.containsKey(key)) {
            throw new ApiException("Invalid role: " + inputRole);
        }

        return ROLE_ALIASES.get(key);
    }

    private static String normalizeRoleKey(String role) {
        return role.toLowerCase().replaceAll("[^a-z0-9]", "").trim();
    }

    private static String normalizeText(String text) {
        return text == null ? "" :
                text.toLowerCase()
                        .replaceAll("[^a-z0-9+.# ]", " ")
                        .replaceAll("\\s+", " ")
                        .trim();
    }

    private String extractText(MultipartFile file) throws Exception {

        String filename = file.getOriginalFilename();

        if (filename != null && filename.toLowerCase().endsWith(".pdf")) {
            try (PDDocument document = PDDocument.load(file.getInputStream())) {
                return new PDFTextStripper().getText(document);
            }
        }

        if (filename != null && filename.toLowerCase().endsWith(".docx")) {
            try (InputStream is = file.getInputStream();
                 XWPFDocument document = new XWPFDocument(is)) {

                StringBuilder text = new StringBuilder();
                document.getParagraphs()
                        .forEach(p -> text.append(p.getText()).append(" "));
                return text.toString();
            }
        }

        throw new ApiException("Only PDF and DOCX files are supported");
    }

    public List<String> getAllRolesList() {
        return new ArrayList<>(COURSE_SKILLS.keySet());
    }
}
