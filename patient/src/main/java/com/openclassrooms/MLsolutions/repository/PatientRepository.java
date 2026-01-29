package com.openclassrooms.MLsolutions.repository;

import com.openclassrooms.MLsolutions.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
