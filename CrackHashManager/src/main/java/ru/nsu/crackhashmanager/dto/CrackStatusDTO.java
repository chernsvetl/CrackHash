package ru.nsu.crackhashmanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
@ToString
public class CrackStatusDTO {

    @JsonProperty(value = "status")
    CrackStatus crackStatus;

    @JsonProperty(value = "data")
    List<String> data;

}
