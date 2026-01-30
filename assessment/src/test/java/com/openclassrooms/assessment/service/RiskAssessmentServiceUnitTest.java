package com.openclassrooms.assessment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.assessment.client.GatewayClient;
import com.openclassrooms.assessment.domain.RiskLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class RiskAssessmentServiceUnitTest {

    private final RiskAssessmentService service = new RiskAssessmentService(mock(GatewayClient.class), new ObjectMapper());

    @Test
    void determineRiskLevel_none_whenNoTriggers() {
        assertEquals(RiskLevel.None, service.determineRiskLevel(59, false, 0));
    }

    @Test
    void determineRiskLevel_borderline_whenOver30_and2to5Triggers() {
        assertEquals(RiskLevel.Borderline, service.determineRiskLevel(59, false, 2));
        assertEquals(RiskLevel.Borderline, service.determineRiskLevel(59, true, 5));
    }

    @Test
    void determineRiskLevel_inDanger_whenUnder30_male_andAtLeast3Triggers() {
        assertEquals(RiskLevel.InDanger, service.determineRiskLevel(25, true, 3));
    }

    @Test
    void determineRiskLevel_inDanger_whenUnder30_female_andAtLeast4Triggers() {
        assertEquals(RiskLevel.InDanger, service.determineRiskLevel(25, false, 4));
    }

    @Test
    void determineRiskLevel_earlyOnser_whenOver30_male_andAtLeast8Triggers() {
        assertEquals(RiskLevel.EarlyOnset, service.determineRiskLevel(59, false, 8));
    }


}
