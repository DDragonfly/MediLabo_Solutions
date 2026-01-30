package com.openclassrooms.assessment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.assessment.client.GatewayClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class TriggerCountingTest {

    private final RiskAssessmentService service = new RiskAssessmentService(mock(GatewayClient.class), new ObjectMapper());

    @Test
    void countTriggersFromText_shouldIgnorePoidsEquelOrLowerThanRecommended() {
        String note = "Le patient déclare qu'il se sent très bien. Poids égal ou inférieur au poids recommandé";
        assertEquals(0, service.countTriggersFromText(note));
    }

    @Test
    void countTriggersFromText_shouldCountTriggers_caseAndAccentsInsensitive() {
        String note = "Hémoglobine A1C. Le patient est fumeur. Cholestérol.";
        assertEquals(3, service.countTriggersFromText(note));
    }

}
