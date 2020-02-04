package com.swisscom.humanresources.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.UUID;

@Value.Immutable
@JsonDeserialize(as = ImmutableEmployee.class)
public interface Employee {
    UUID id();
    String firstname();
    String lastname();
    String organization();
    LocalDate birthdate();

    static ImmutableEmployee.Builder builder() {
        return ImmutableEmployee.builder();
    }
}
