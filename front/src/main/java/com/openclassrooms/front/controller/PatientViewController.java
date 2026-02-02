package com.openclassrooms.front.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.front.client.GatewayClient;
import com.openclassrooms.front.dto.NoteDto;
import com.openclassrooms.front.dto.NoteForm;
import com.openclassrooms.front.dto.PatientDto;
import com.openclassrooms.front.dto.PatientForm;
import com.openclassrooms.front.session.SessionAuth;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
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

    @GetMapping("/patients/new")
    public String newPatientForm(Model model, HttpSession session) {
        SessionAuth auth = (SessionAuth) session.getAttribute("auth");
        if (auth == null) return "redirect:/login";

        model.addAttribute("mode", "create");
        model.addAttribute("form", new PatientForm());
        return "patient-form";
    }

    @PostMapping("/patients")
    public String createPatient(
            @ModelAttribute("form") PatientForm form,
            Model model,
            HttpSession session
    ) {
        SessionAuth auth = (SessionAuth) session.getAttribute("auth");
        if (auth == null) return "redirect:/login";

        if (form.getFirstName() == null || form.getFirstName().isBlank()
                || form.getLastName() == null || form.getLastName().isBlank()) {
            model.addAttribute("error", "First name and last name are required.");
            model.addAttribute("mode", "create");
            return "patient-form";
        }
        gatewayClient.post("/patients", form, auth);
        return "redirect:/patients";
    }

    @GetMapping("/patients/{id}/edit")
    public String editPatientForm(@PathVariable Long id, Model model, HttpSession session) throws Exception {
        SessionAuth auth = (SessionAuth) session.getAttribute("auth");
        if (auth == null) return "redirect:/login";

        ResponseEntity<String> response = gatewayClient.get("/patients/" + id, auth);
        PatientDto patient = objectMapper.readValue(response.getBody(), PatientDto.class);

        PatientForm form = new PatientForm();
        form.setFirstName(patient.firstName());
        form.setLastName(patient.lastName());
        form.setBirthDate(patient.birthDate());
        form.setGender(patient.gender());
        form.setAddress(patient.address());
        form.setPhone(patient.phone());

        model.addAttribute("form", form);
        model.addAttribute("patientId", id);
        model.addAttribute("mode", "edit");
        return "patient-form";
    }

    @PostMapping("/patients/{id}")
    public String updatePatient(
            @PathVariable Long id,
            @ModelAttribute("form") PatientForm form,
            Model model,
            HttpSession session
    ) {
        SessionAuth auth = (SessionAuth) session.getAttribute("auth");
        if (auth == null) return "redirect:/login";

        if (form.getFirstName() == null || form.getFirstName().isBlank()
                || form.getLastName() == null || form.getLastName().isBlank()) {
            model.addAttribute("error", "First name and last name are required.");
            model.addAttribute("patientId", id);
            model.addAttribute("mode", "edit");
            return "patient-form";
        }

        gatewayClient.put("/patients/" + id, form, auth);
        return "redirect:/patients";
    }
}
