package com.openclassrooms.assessment.dto;

public record NoteDto(
        String id,
        Integer patId,
        String patient,
        String note
) {
}
