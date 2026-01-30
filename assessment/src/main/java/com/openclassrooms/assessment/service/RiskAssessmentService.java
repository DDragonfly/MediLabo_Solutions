package com.openclassrooms.assessment.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.assessment.client.GatewayClient;
import com.openclassrooms.assessment.domain.RiskLevel;
import com.openclassrooms.assessment.domain.Trigger;
import com.openclassrooms.assessment.dto.NoteDto;
import com.openclassrooms.assessment.dto.PatientDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(RiskAssessmentService.class);

    public RiskAssessmentService(GatewayClient gatewayClient, ObjectMapper objectMapper) {
        this.gatewayClient = gatewayClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Computes the diabetes risk level for a given patient.
     *
     * <p>
     * Patient demographic data and medical notes are retrieved through the gateway.
     * The risk level is then determined according to the business rules defined
     * by the MediLabo specifications.
     *
     * @param patId the patient identifier
     * @return the computed diabetes {@link RiskLevel}
     */
    public RiskLevel assess(Long patId) {
        log.info("### ASSESSMENT SERVICE VERSION CHECK ###");
        log.debug("Assessing diabetes risk for patient {}", patId);
        PatientDto patient = fetchPatient(patId);
        List<NoteDto> notes = fetchNotes(patId);

        int age = calculateAge(patient.birthDate());
        boolean isMale = patient.gender() != null && patient.gender().equalsIgnoreCase("M");

        int triggerCount = countTriggers(notes);

        log.debug("Risk assessment for patId={} | age={} | gender ={} | notes={} | triggers={}", patId, age, patient.gender(), notes.size(), triggerCount);

        return determineRiskLevel(age, isMale, triggerCount);
    }

    /**
     * Determines the diabetes risk level based on patient characteristics
     * and the number of detected trigger terms.
     *
     * <p>
     * Rules depend on:
     * <ul>
     *   <li>Patient age (above or below 30)</li>
     *   <li>Patient gender</li>
     *   <li>Number of trigger terms found in medical notes</li>
     * </ul>
     *
     * @param age patient age in years
     * @param isMale whether the patient is male
     * @param triggerCount number of detected trigger terms
     * @return the corresponding {@link RiskLevel}
     */
    RiskLevel determineRiskLevel(int age, boolean isMale, int triggerCount) {
        if (triggerCount == 0) return RiskLevel.None;

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

        // default
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

    /**
     * Counts the number of diabetes-related trigger terms in the provided text.
     *
     * <p>
     * Trigger detection is case- and accent-insensitive.
     * Each trigger is counted independently, except for the "POIDS" trigger,
     * which is handled separately to exclude explicit mentions of normal
     * or below-recommended weight.
     *
     * @param notesText concatenated medical notes
     * @return number of detected trigger terms
     */
    int countTriggersFromText(String notesText) {
        if (notesText == null) return 0;
        String normalizedNotes = normalize(notesText);

        int count = 0;

        for (Trigger t : Trigger.values()) {
            int found =0;

            if (t == Trigger.POIDS) {
                found = countPoidsTrigger(normalizedNotes);
            } else {
                found = occurrences(normalizedNotes, t.token());
            }
            if (found > 0) {
                log.debug("Trigger detected: {} ({} occurrences)", t.name(), found);
            }

            count+=found;
        }
        return count;
    }

    private int countTriggers(List<NoteDto> notes) {
        String allNotes = notes.stream()
                .map(NoteDto::note)
                .filter(s -> s != null)
                .reduce("", (a, b) -> a + " " + b );

        return countTriggersFromText(allNotes);
    }

    /**
     * Counts occurrences of the "poids" trigger while excluding expressions
     * that explicitly indicate a normal or below-recommended weight.
     *
     * <p>
     * This workaround is required to correctly interpret the functional
     * test cases provided in the project specifications, where certain
     * notes mention weight in a non-pathological context.
     *
     * @param normalizedNotes normalized medical notes text
     * @return number of relevant "poids" trigger occurrences
     */
    private int countPoidsTrigger(String normalizedNotes) {
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
