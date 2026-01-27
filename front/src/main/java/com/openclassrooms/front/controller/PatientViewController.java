package com.openclassrooms.front.controller;

import com.openclassrooms.front.client.GatewayClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

@Controller
public class PatientViewController {

    private final GatewayClient gatewayClient;

    public PatientViewController(GatewayClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    @GetMapping("/patients")
    public String listPatients(Model model) {
        ResponseEntity<String> response = gatewayClient.get("/patients");
        model.addAttribute("patientsJson", response.getBody());
        return "patients";
    }

    @GetMapping("/patients/{id}")
    public String patientDetail(@PathVariable Long id, Model model) {
        ResponseEntity<String> response = gatewayClient.get("/patients/" + id);
        model.addAttribute("patientJson", response.getBody());
        model.addAttribute("patientId", id);
        return "patient-detail";
    }
}
