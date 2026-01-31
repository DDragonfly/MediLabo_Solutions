package com.openclassrooms.front.controller;

import com.openclassrooms.front.client.GatewayClient;
import com.openclassrooms.front.session.SessionAuth;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PatientViewController {

    private final GatewayClient gatewayClient;

    public PatientViewController(GatewayClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    @GetMapping("/patients")
    public String listPatients(Model model, HttpSession session) {
        SessionAuth auth  = (SessionAuth) session.getAttribute("auth");
        if (auth == null) {
            return "redirect:/login";
        }
        ResponseEntity<String> response = gatewayClient.get("/patients", auth);
        model.addAttribute("patientsJson", response.getBody());
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
}
