package com.openclassrooms.front.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.front.client.GatewayClient;
import com.openclassrooms.front.dto.NoteDto;
import com.openclassrooms.front.dto.PatientDto;
import com.openclassrooms.front.session.SessionAuth;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class PatientViewController {

    private final GatewayClient gatewayClient;
    private final ObjectMapper objectMapper;

    public PatientViewController(GatewayClient gatewayClient, ObjectMapper objectMapper) {
        this.gatewayClient = gatewayClient;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/patients")
    public String listPatients(Model model, HttpSession session) throws Exception {
        SessionAuth auth  = (SessionAuth) session.getAttribute("auth");
        if (auth == null) {
            return "redirect:/login";
        }
        ResponseEntity<String> response = gatewayClient.get("/patients", auth);

        List<PatientDto> patients = objectMapper.readValue(
                response.getBody(),
                new TypeReference<List<PatientDto>>() {}
        );

        model.addAttribute("patients", patients);
        return "patients";
    }

    @GetMapping("/patients/{id}")
    public String patientDetail(@PathVariable Long id, Model model, HttpSession session) {
        SessionAuth auth  = (SessionAuth) session.getAttribute("auth");
        if (auth == null) {
            return "redirect:/login";
        }

        ResponseEntity<String> response = gatewayClient.get("/patients/" + id, auth);
        ResponseEntity<String> notesResponse = gatewayClient.get("/notes/patient/" + id, auth);
        ResponseEntity<String> assessmentsResponse = gatewayClient.get("/assessments/patient/" + id, auth);

        model.addAttribute("patientJson", response.getBody());
        model.addAttribute("notesJson", notesResponse.getBody());
        model.addAttribute("assessmentJson", assessmentsResponse.getBody());
        model.addAttribute("patientId", id);
        return "patient-detail";
    }

    @GetMapping("/patients/{id}/notes")
    public String patientNotes(@PathVariable Long id, Model model, HttpSession session) throws Exception {
        SessionAuth auth  = (SessionAuth) session.getAttribute("auth");
        if (auth == null) {
            return "redirect:/login";
        }

        ResponseEntity<String> patientResp = gatewayClient.get("/patients/" + id, auth);
        ResponseEntity<String> notesResp = gatewayClient.get("/notes/patient/" + id, auth);

        PatientDto patient = objectMapper.readValue(patientResp.getBody(), PatientDto.class);
        List<NoteDto> notes = objectMapper.readValue(notesResp.getBody(), new TypeReference<List<NoteDto>>() {});

        model.addAttribute("patient", patient);
        model.addAttribute("notes", notes);
        return "patient-notes";
    }

}
