package com.openclassrooms.MLsolutions.service;

import com.openclassrooms.MLsolutions.entity.Patient;
import com.openclassrooms.MLsolutions.repository.PatientRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for managing patients.
 * <p>
 *     Contains the business logic related to Patient entities
 *     and acts as an intermediary between controller and repo.
 * </p>
 */
@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Retrieves all patients.
     *
     * @return list of all patients
     */
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Retrieves a patient by its id.
     *
     * @param id patient identifier
     * @return the found patient
     * @throws IllegalArgumentException if the patient does not exist
     */
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient with id: " + id + " not found"));
    }

    /**
     * Creates a new patient.
     *
     * @param patient to create
     * @return created patient
     */
    public Patient createPatient(Patient patient) {
        patient.setId(null); // pour raisons de securité
        return patientRepository.save(patient);
    }

    /**
     * Updates an existing patient
     *
     * @param id of the patient to update
     * @param updatedPatient new patient data
     * @return updated patient
     */
    public Patient updatePatient(Long id, Patient updatedPatient) {
        Patient existingP = getPatientById(id);

        existingP.setFirstName(updatedPatient.getFirstName());
        existingP.setLastName(updatedPatient.getLastName());
        existingP.setBirthDate(updatedPatient.getBirthDate());
        existingP.setGender(updatedPatient.getGender());
        existingP.setAddress(updatedPatient.getAddress());
        existingP.setPhone(updatedPatient.getPhone());

        return patientRepository.save(existingP);
    }

    /**
     * Deletes a patient by its id.
     *
     * @param id of the patient to delete
     */
    public void deletePatient(Long id) {
        getPatientById(id);
        patientRepository.deleteById(id);
    }
}
