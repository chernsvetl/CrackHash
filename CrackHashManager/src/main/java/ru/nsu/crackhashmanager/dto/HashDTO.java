package ru.nsu.crackhashmanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
@ToString
public class HashDTO {

    @JsonProperty(value = "hash")
    String hash;

    @JsonProperty(value = "maxLength")
    Integer maxLength;

}
