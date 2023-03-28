package ru.nsu.crackhashworker.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
public class GenerationParameters {

    Integer partNumber;

    Integer partCount;

    Integer maxLength;

    Integer alphabetCardinality;

}
