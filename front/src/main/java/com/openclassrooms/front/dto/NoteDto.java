package com.openclassrooms.front.dto;

public record NoteDto(
        String id,
        Integer patId,
        String patient,
        String note
) {
}
