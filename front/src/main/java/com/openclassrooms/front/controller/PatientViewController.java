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

/**
 * MVC controller responsible for patient management views.
 *
 * <p>
 * This controller allows users to:
 * <ul>
 *     <li>View the list of patients</li>
 *     <li>Create a new patient</li>
 *     <li>Edit existing patient information</li>
 * </ul>
 *
 * <p>
 * All operations are performed via the API Gateway to ensure
 * proper separation between front-end and back-end services.
 */
@Controller
public class PatientViewController {

    private final GatewayClient gatewayClient;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a PatientViewController.
     *
     * @param gatewayClient client used to communicate with the API Gateway
     * @param objectMapper mapper used to deserialize JSON responses
     */
    public PatientViewController(GatewayClient gatewayClient, ObjectMapper objectMapper) {
        this.gatewayClient = gatewayClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Displays the list of all patients.
     *
     * @param model   Spring MVC model
     * @param session HTTP session
     * @return the patients view or a redirect to login
     */
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

    /**
     * Displays the form to create a new patient.
     *
     * @param model   Spring MVC model
     * @param session HTTP session
     * @return the patient-form view or a redirect to login
     */
    @GetMapping("/patients/new")
    public String newPatientForm(Model model, HttpSession session) {
        SessionAuth auth = (SessionAuth) session.getAttribute("auth");
        if (auth == null) return "redirect:/login";

        model.addAttribute("mode", "create");
        model.addAttribute("form", new PatientForm());
        return "patient-form";
    }

    /**
     * Handles the creation of a new patient.
     *
     * @param form    patient form submitted by the user
     * @param model   Spring MVC model
     * @param session HTTP session
     * @return redirect to the patients list
     */
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

    /**
     * Displays the form to edit an existing patient.
     *
     * @param id      patient identifier
     * @param model   Spring MVC model
     * @param session HTTP session
     * @return the patient-form view or a redirect to login
     */
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

    /**
     * Handles the update of an existing patient.
     *
     * @param id      patient identifier
     * @param form    updated patient form
     * @param model   Spring MVC model
     * @param session HTTP session
     * @return redirect to the patients list
     */
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
