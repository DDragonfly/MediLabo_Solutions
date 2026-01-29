package com.openclassrooms.assessment.controller;

import com.openclassrooms.assessment.domain.RiskLevel;
import com.openclassrooms.assessment.service.RiskAssessmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assessments")
public class AssessmentController {

    private final RiskAssessmentService riskAssessmentService;

    public AssessmentController(RiskAssessmentService riskAssessmentService) {
        this.riskAssessmentService = riskAssessmentService;
    }

    @GetMapping("/patient/{patId}")
    public ResponseEntity<AssessmentResponse> assess(@PathVariable Long patId) {
        RiskLevel level = riskAssessmentService.assess(patId);
        return ResponseEntity.ok(new AssessmentResponse(patId, level.name()));
    }

    public record AssessmentResponse(Long patId, String riskLevel) {}
}
