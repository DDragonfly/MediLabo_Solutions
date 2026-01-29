package com.openclassrooms.assessment.dto;

import java.time.LocalDate;

public record PatientDto(
        Long id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String gender
) {
}
