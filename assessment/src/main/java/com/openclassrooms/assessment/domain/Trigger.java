package com.openclassrooms.assessment.domain;

public enum Trigger {
    HEMOGLOBINE_A1C("hemoglobine a1c"),
    MICROALBUMINE("microalbumine"),
    TAILLE("taille"),
    POIDS("poids"),
    FUMEUR("fumeur"),
    FUMEUSE("fumeuse"),
    ANORMAL("anormal"),
    CHOLESTEROL("cholesterol"),
    VERTIGES("vertige"),
    RECHUTE("rechute"),
    REACTION("reaction"),
    ANTICORPS("anticorps");

    private final String token;

    Trigger(String token) {
        this.token = token;
    }

    public String token() {
        return token;
    }
}
