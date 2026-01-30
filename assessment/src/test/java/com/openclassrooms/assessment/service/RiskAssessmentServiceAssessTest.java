package com.openclassrooms.assessment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.assessment.client.GatewayClient;
import com.openclassrooms.assessment.domain.RiskLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RiskAssessmentServiceAssessTest {

    private GatewayClient gatewayClient;
    private RiskAssessmentService service;

    @BeforeEach
    void setup() {
        gatewayClient = mock(GatewayClient.class);
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        service = new RiskAssessmentService(gatewayClient, mapper);
    }

    @Test
    void assess_shouldReturnNone_forPatient1() {

        when(gatewayClient.get("/patients/1"))
                .thenReturn(ResponseEntity.ok("""
                        {
                          "id": 1,
                          "firstName": "Test",
                          "lastName": "TestNone",
                          "birthDate": "1966-12-31",
                          "gender": "F"
                        }
                        """));

        when(gatewayClient.get("/notes/patient/1"))
                .thenReturn(ResponseEntity.ok("""
                        [
                          {
                            "id": "n1",
                            "patId": 1,
                            "patient": "TestNone",
                            "note": "Le patient déclare qu'il se sent très bien. Poids égal ou inférieur au poids recommandé"
                          }
                        ]
                        """));

        RiskLevel level = service.assess(1L);
        assertEquals(RiskLevel.None, level);

        verify(gatewayClient).get("/patients/1");
        verify(gatewayClient).get("/notes/patient/1");
        verifyNoMoreInteractions(gatewayClient);
    }

    @Test
    void assess_shouldReturnEarlyOnset_forPatient4() {
        when(gatewayClient.get("/patients/4"))
                .thenReturn(ResponseEntity.ok("""
                        {
                          "id": 4,
                          "firstName": "Test",
                          "lastName": "TestEarlyOnset",
                          "birthDate": "2002-06-28",
                          "gender": "F"
                        }
                        """));


        when(gatewayClient.get("/notes/patient/4"))
                .thenReturn(ResponseEntity.ok("""
                        [
                          {"id":"a","patId":4,"patient":"TestEarlyOnset","note":"Le patient déclare qu'il lui est devenu difficile de monter les escaliers. Il se plaint également d’être essoufflé. Tests de laboratoire indiquant que les anticorps sont élevés. Réaction aux médicaments."},
                          {"id":"b","patId":4,"patient":"TestEarlyOnset","note":"Le patient déclare qu'il a mal au dos lorsqu'il reste assis pendant longtemps."},
                          {"id":"c","patId":4,"patient":"TestEarlyOnset","note":"Le patient déclare avoir commencé à fumer depuis peu. Hémoglobine A1C supérieure au niveau recommandé."},
                          {"id":"d","patId":4,"patient":"TestEarlyOnset","note":"Taille, Poids, Cholestérol, Vertige et Réaction"}
                        ]
                        """));

        RiskLevel level = service.assess(4L);
        assertEquals(RiskLevel.EarlyOnset, level);

        verify(gatewayClient).get("/patients/4");
        verify(gatewayClient).get("/notes/patient/4");
        verifyNoMoreInteractions(gatewayClient);
    }
}
