package com.openclassrooms.assessment.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.assessment.client.GatewayClient;
import com.openclassrooms.assessment.domain.RiskLevel;
import com.openclassrooms.assessment.domain.Trigger;
import com.openclassrooms.assessment.dto.NoteDto;
import com.openclassrooms.assessment.dto.PatientDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Locale;

@Service
public class RiskAssessmentService {

    private final GatewayClient gatewayClient;
    private final ObjectMapper objectMapper;

    public RiskAssessmentService(GatewayClient gatewayClient, ObjectMapper objectMapper) {
        this.gatewayClient = gatewayClient;
        this.objectMapper = objectMapper;
    }

    public RiskLevel assess(Long patId) {
        PatientDto patient = fetchPatient(patId);
        List<NoteDto> notes = fetchNotes(patId);

        int age = calculateAge(patient.birthDate());
        boolean isMale = patient.gender() != null && patient.gender().equalsIgnoreCase("M");

        int triggerCount = countTriggers(notes);

        System.out.println("patId=" + patId
                + " age=" + age
                + " gender=" + patient.gender()
                + " notesCount=" + notes.size()
                + " triggerCount=" + triggerCount);

        notes.forEach(n -> System.out.println("NOTE: " + n.note()));


        if (triggerCount == 0) {
            return RiskLevel.None;
        }

        // borderline
        if (age > 30 && triggerCount >= 2 && triggerCount <= 5) {
            return RiskLevel.Borderline;
        }

        // earlyonset
        if (age < 30) {
            if (isMale && triggerCount >= 5) return RiskLevel.EarlyOnset;
            if (!isMale && triggerCount >= 7) return RiskLevel.EarlyOnset;
        } else {
            if (triggerCount >= 8) return RiskLevel.EarlyOnset;
        }

        // indanger
        if (age < 30) {
            if (isMale && triggerCount >= 3) return RiskLevel.InDanger;
            if (!isMale && triggerCount >= 4) return RiskLevel.InDanger;
        } else {
            if (triggerCount == 6 || triggerCount == 7) return RiskLevel.InDanger;
        }

        if (age > 30) return RiskLevel.Borderline;
        return RiskLevel.InDanger;
    }

    private PatientDto fetchPatient(Long patId) {
        ResponseEntity<String> response = gatewayClient.get("/patients/" + patId);
        try {
            return objectMapper.readValue(response.getBody(), PatientDto.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse patient response for patId=" + patId, e);
        }
    }

    private List<NoteDto> fetchNotes(Long patId) {
        ResponseEntity<String> response = gatewayClient.get("/notes/patient/" + patId);
        try {
            return objectMapper.readValue(response.getBody(), new TypeReference<List<NoteDto>>() {
            });
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse notes response for patId=" + patId, e);
        }
    }

    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private int countTriggers(List<NoteDto> notes) {
        String allNotes = notes.stream()
                .map(NoteDto::note)
                .filter(s -> s != null)
                .reduce("", (a, b) -> a + " " + b );

        String normalizedNotes = normalize(allNotes);

        int count = 0;

        for (Trigger t : Trigger.values()) {
            int found = 0;

            if (t == Trigger.POIDS) {
                found += countPoidsTrigger(normalizedNotes);
            } else {
                found = occurrences(normalizedNotes, t.token());
            }
            if (found > 0) {
                System.out.println("TRIGGER FOUND -> " + t.name()
                + " (" + found + "occurrence(s))");
            }
            count += found;
        }
        return count;
    }

    private int countPoidsTrigger(String normalizedNotes) {
        // astuce POIDS
        String filtered = normalizedNotes
                .replaceAll("\\bpoids\\b\\s+egal\\s+ou\\s+inferieur\\s+au\\s+poids\\s+recommande", " ")
                .replaceAll("\\bpoids\\b\\s+normal", " ");

        return occurrences(filtered, "poids");
    }


    private String normalize(String s) {
        String lower  = s.toLowerCase(Locale.ROOT);
        String noAccents = Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return noAccents;
    }

    private int occurrences(String text, String token) {
        int idx = 0;
        int n = 0;
        while ((idx = text.indexOf(token, idx)) != -1) {
            n++;
            idx += token.length();
        }
        return n;
    }
}
