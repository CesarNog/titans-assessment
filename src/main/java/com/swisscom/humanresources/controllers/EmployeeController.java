package com.swisscom.humanresources.controllers;

import com.swisscom.humanresources.models.Employee;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@RestController
public class EmployeeController {

    @GetMapping(value = "/employees/{uuid}")
    Mono<Employee> get(@PathVariable("uuid") UUID uuid) {
        return Mono.just(Employee.builder()
                .id(uuid)
                .firstname("peter")
                .lastname("muster")
                .birthdate(LocalDate.of(1980,8,1))
                .organization("INI-CLD")
                .build());
    }

    @GetMapping(value = "/employees")
    public Mono<Collection<Employee>> search(@RequestParam(value = "search") String search) {
        return Mono.just(Arrays.asList(
                Employee.builder()
                        .id(UUID.randomUUID())
                        .firstname("Charlie")
                        .lastname("Chambers")
                        .birthdate(LocalDate.of(1993,11,8))
                        .organization("Facility management")
                        .build()));
    }
}
