package ru.nsu.crackhashmanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.function.BinaryOperator;

public enum CrackStatus {

    @JsonProperty("ERROR")
    ERROR(0b00),

    @JsonProperty("IN_PROGRESS")
    IN_PROGRESS(0b01),

    @JsonProperty("READY")
    READY(0b11);

    public static final BinaryOperator<CrackStatus> statusConjunction =
        (a, b) -> CrackStatus.findByValue(a.value & b.value);

    public static final BinaryOperator<CrackStatus> statusDisjunction =
        (a, b) -> CrackStatus.findByValue(a.value | b.value);

    private final int value;

    CrackStatus(int value) {
        this.value = value;
    }

    private static CrackStatus findByValue(int value) {
        for (CrackStatus status : CrackStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }

        throw new IllegalArgumentException("Not found CrackStatus by selected value");
    }

}
