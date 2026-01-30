package com.openclassrooms.assessment.controller;

import com.openclassrooms.assessment.domain.RiskLevel;
import com.openclassrooms.assessment.service.RiskAssessmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;


@WebMvcTest(AssessmentController.class)
@Import(TestSecurityConfig.class)
public class AssessmentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RiskAssessmentService riskAssessmentService;

    @Test
    void assess_shouldReturn200_andJsonPayload() throws Exception {

        when(riskAssessmentService.assess(1L)).thenReturn(RiskLevel.None);

        mockMvc.perform(get("/assessments/patient/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.patId").value(1))
                .andExpect(jsonPath("$.riskLevel").value("None"));
    }

    @Test
    void assess_shouldReturnRiskLevelEarlyOnSet() throws Exception {
        when(riskAssessmentService.assess(4L)).thenReturn(RiskLevel.EarlyOnset);

        mockMvc.perform(get("/assessments/patient/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patId").value(4))
                .andExpect(jsonPath("$.riskLevel").value("EarlyOnset"));
    }

}
