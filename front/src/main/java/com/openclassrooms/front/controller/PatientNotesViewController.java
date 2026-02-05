package com.openclassrooms.front.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.front.client.GatewayClient;
import com.openclassrooms.front.dto.AssessmentDto;
import com.openclassrooms.front.dto.NoteDto;
import com.openclassrooms.front.dto.NoteForm;
import com.openclassrooms.front.dto.PatientDto;
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
 * MVC controller responsible for managing the views related to
 * patient medical notes and risk assessment.
 *
 * <p>
 * This controller handles:
 * <ul>
 *     <li>Displaying all notes for a given patient</li>
 *     <li>Creating new medical notes</li>
 *     <li>Editing existing medical notes</li>
 *     <li>Displaying the diabetes risk assessment associated with a patient</li>
 * </ul>
 *
 * <p>
 * All data is retrieved through the API Gateway, ensuring that
 * the front-end remains decoupled from the underlying microservices.
 */
@Controller
public class PatientNotesViewController {
    private final GatewayClient gatewayClient;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a PatientNotesViewController.
     *
     * @param gatewayClient client used to communicate with the API Gateway
     * @param objectMapper mapper used to deserialize JSON responses
     */
    public PatientNotesViewController(GatewayClient gatewayClient, ObjectMapper objectMapper) {
        this.gatewayClient = gatewayClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Displays all medical notes and the risk assessment for a given patient.
     *
     * @param id      patient identifier
     * @param model   Spring MVC model
     * @param session HTTP session used for authentication
     * @return the patient-notes view or a redirect to login if unauthenticated
     */
    @GetMapping("/patients/{id}/notes")
    public String patientNotes(@PathVariable Long id, Model model, HttpSession session) throws Exception {
        SessionAuth auth  = (SessionAuth) session.getAttribute("auth");
        if (auth == null) {
            return "redirect:/login";
        }

        ResponseEntity<String> patientResp = gatewayClient.get("/patients/" + id, auth);
        ResponseEntity<String> notesResp = gatewayClient.get("/notes/patient/" + id, auth);
        ResponseEntity<String> assessResp = gatewayClient.get("/assessments/patient/" + id, auth);

        PatientDto patient = objectMapper.readValue(patientResp.getBody(), PatientDto.class);
        List<NoteDto> notes = objectMapper.readValue(notesResp.getBody(), new TypeReference<List<NoteDto>>() {});
        AssessmentDto assessment = objectMapper.readValue(assessResp.getBody(), AssessmentDto.class);

        model.addAttribute("patient", patient);
        model.addAttribute("notes", notes);
        model.addAttribute("assessment", assessment);
        return "patient-notes";
    }

    /**
     * Displays the form used to add a new medical note for a patient.
     *
     * @param id      patient identifier
     * @param model   Spring MVC model
     * @param session HTTP session
     * @return the note-form view or a redirect to login
     */
    @GetMapping("/patients/{id}/notes/add")
    public String addNoteForm(@PathVariable Long id, Model model, HttpSession session) throws Exception {
        SessionAuth auth  = (SessionAuth) session.getAttribute("auth");
        if (auth == null) return "redirect:/login";

        ResponseEntity<String> patientResp = gatewayClient.get("/patients/" + id, auth);
        PatientDto patient = objectMapper.readValue(patientResp.getBody(), PatientDto.class);

        model.addAttribute("mode", "create");
        model.addAttribute("patient", patient);
        model.addAttribute("form", new NoteForm());
        return "note-form";
    }

    /**
     * Handles the creation of a new medical note.
     *
     * @param id      patient identifier
     * @param form    note form submitted by the user
     * @param model   Spring MVC model
     * @param session HTTP session
     * @return redirect to the patient's notes page
     */
    @PostMapping("/patients/{id}/notes")
    public String createNote(@PathVariable Long id, @ModelAttribute("form") NoteForm form, Model model, HttpSession session) throws Exception {
        SessionAuth auth  = (SessionAuth) session.getAttribute("auth");
        if (auth == null) return "redirect:/login";

        if (form.getNote() == null || form.getNote().isBlank()) {
            ResponseEntity<String> patientResp = gatewayClient.get("/patients/" + id, auth);
            PatientDto patient = objectMapper.readValue(patientResp.getBody(), PatientDto.class);

            model.addAttribute("mode", "create");
            model.addAttribute("patient", patient);
            model.addAttribute("error", "Note content is required.");
            return "note-form";
        }

        PatientDto patient = objectMapper.readValue(gatewayClient.get("/patients/" + id, auth).getBody(), PatientDto.class);

        var payload = new HashMap<String, Object>();
        payload.put("patId", id.intValue());
        payload.put("patient", patient.lastName());
        payload.put("note", form.getNote());

        gatewayClient.post("/notes", payload, auth);

        return "redirect:/patients/" + id + "/notes";
    }

    /**
     * Displays the form used to edit an existing medical note.
     *
     * @param id      patient identifier
     * @param noteId  note identifier
     * @param model   Spring MVC model
     * @param session HTTP session
     * @return the note-form view or a redirect to login
     */
    @GetMapping("/patients/{id}/notes/{noteId}/edit")
    public String editNoteForm(@PathVariable Long id, @PathVariable String noteId, Model model, HttpSession session) throws Exception {
        SessionAuth auth  = (SessionAuth) session.getAttribute("auth");
        if (auth == null) return "redirect:/login";

        ResponseEntity<String> patientResp = gatewayClient.get("/patients/" + id, auth);
        PatientDto patient = objectMapper.readValue(patientResp.getBody(), PatientDto.class);

        ResponseEntity<String> notesResp = gatewayClient.get("/notes/" + noteId, auth);
        NoteDto note = objectMapper.readValue(notesResp.getBody(), NoteDto.class);

        NoteForm form = new NoteForm();
        form.setNote(note.note());

        model.addAttribute("mode", "edit");
        model.addAttribute("patient", patient);
        model.addAttribute("noteId", noteId);
        model.addAttribute("form", form);
        return "note-form";
    }

    /**
     * Handles the update of an existing medical note.
     *
     * @param id      patient identifier
     * @param noteId  note identifier
     * @param form    updated note form
     * @param model   Spring MVC model
     * @param session HTTP session
     * @return redirect to the patient's notes page
     */
    @PostMapping("/patients/{id}/notes/{noteId}")
    public String updateNote(@PathVariable Long id, @PathVariable String noteId, @ModelAttribute("form") NoteForm form, Model model, HttpSession session) throws Exception {
        SessionAuth auth  = (SessionAuth) session.getAttribute("auth");
        if (auth == null) return "redirect:/login";

        if (form.getNote() == null || form.getNote().isBlank()) {
            ResponseEntity<String> patientResp = gatewayClient.get("/patients/" + id, auth);
            PatientDto patient = objectMapper.readValue(patientResp.getBody(), PatientDto.class);

            model.addAttribute("mode", "edit");
            model.addAttribute("patient", patient);
            model.addAttribute("noteId", noteId);
            model.addAttribute("error", "Note content is required.");
            return "note-form";
        }

        NoteDto existing = objectMapper.readValue(gatewayClient.get("/notes/" + noteId, auth).getBody(), NoteDto.class);

        var payload = new HashMap<String, Object>();
        payload.put("id", noteId);
        payload.put("patId", existing.patId());
        payload.put("patient", existing.patient());
        payload.put("note", form.getNote());

        gatewayClient.put("/notes/" + noteId, payload, auth);

        return "redirect:/patients/" + id + "/notes";
    }
}
