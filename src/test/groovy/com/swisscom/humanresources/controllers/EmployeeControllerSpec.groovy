package com.swisscom.humanresources.controllers

import com.swisscom.humanresources.models.Employee
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.env.Environment
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification

import java.time.LocalDate

import static java.time.Duration.ofMillis

@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerSpec extends Specification {

    @Autowired
    Environment springEnv

    protected WebTestClient webTestClient

    @LocalServerPort
    protected int port

    protected String getBaseUrl() {
        "http://localhost:" + port.toString()
    }

    void setup() {
        webTestClient = WebTestClient.bindToServer().
                responseTimeout(ofMillis(36000)).
                baseUrl(getBaseUrl()).
                defaultHeaders({
                    header ->
                        header.setContentType(MediaType.APPLICATION_JSON)
                        header.setAccept([MediaType.APPLICATION_JSON])
                }).
                build()
    }

    void 'should GET employee by id'() {
        given:
        UUID uuid = UUID.randomUUID();

        when:
        def responseSpec = webTestClient.get()
                .uri(String.format("/employees/%s", uuid))
                .exchange()

        then:
        noExceptionThrown()
        responseSpec.expectStatus().is2xxSuccessful();
        Employee body = responseSpec.returnResult(Employee).responseBody.blockFirst();
        body.id() == uuid
        body.firstname() == "peter"
        body.lastname() == "muster"
        body.birthdate() == LocalDate.of(1980,8,1)
        body.organization() == "INI-CLD"
    }

    void 'should SEARCH employee by first name'() {
        given:
        String search = "firstname:Charlie"

        when:
        def responseSpec = webTestClient.get()
                .uri(String.format("/employees?search=%s", search))
                .exchange()

        then:
        noExceptionThrown()
        responseSpec.expectStatus().is2xxSuccessful();

        and:
        Collection<Employee> body = responseSpec.expectBodyList(Employee).returnResult().responseBody
        body.size() == 1
        body.first() != null

        and:
        Employee employee = body.first()
        employee.firstname() == "Charlie"
        employee.lastname() == "Chambers"
        employee.birthdate() == LocalDate.of(1993,11,8)
    }

    void 'should SEARCH employee by last name (multiple)'() {
        given:
        String search = "lastname:Broom"

        when:
        def responseSpec = webTestClient.get()
                .uri(String.format("/employees?search=%s", search))
                .exchange()

        then:
        noExceptionThrown()
        responseSpec.expectStatus().is2xxSuccessful();

        and:
        Collection<Employee> body = responseSpec.expectBodyList(Employee).returnResult().responseBody
        body.size() == 2;

        and:
        Employee employee = body.find { e -> e.firstname() == "Bert" }
        employee.lastname() == "Broom"
        employee.birthdate() == LocalDate.of(1980,10,1)

        and:
        Employee employee2 = body.find { e -> e.firstname() == "Ben" }
        employee2.lastname() == "Broom"
        employee2.birthdate() == LocalDate.of(1983,4,2)
    }

    void 'should SEARCH employee by last name (single)'() {
        given:
        String search = "lastname:Doorbell"

        when:
        def responseSpec = webTestClient.get()
                .uri(String.format("/employees?search=%s", search))
                .exchange()

        then:
        noExceptionThrown()
        responseSpec.expectStatus().is2xxSuccessful();

        and:
        Collection<Employee> body = responseSpec.expectBodyList(Employee).returnResult().responseBody
        body.size() == 1
        body.first() != null

        and:
        Employee employee = body.first()
        employee.firstname() == "Dora"
        employee.lastname() == "Doorbell"
        employee.birthdate() == LocalDate.of(1992,10,20)
    }

    void 'should SEARCH employee by organization'() {
        given:
        String search = "organization:Facility management"

        when:
        def responseSpec = webTestClient.get()
                .uri(String.format("/employees?search=%s", search))
                .exchange()

        then:
        noExceptionThrown()
        responseSpec.expectStatus().is2xxSuccessful();

        and:
        Collection<Employee> body = responseSpec.expectBodyList(Employee).returnResult().responseBody
        body.size() == 1
        body.first() != null

        and:
        Employee employee = body.first()
        employee.firstname() == "Charlie"
        employee.lastname() == "Chambers"
        employee.birthdate() == LocalDate.of(1993,11,8)
    }

    void 'should SEARCH employee by last name and first name'() {
        given:
        String search = "firstname:Dora,lastname:Doorbell"

        when:
        def responseSpec = webTestClient.get()
                .uri(String.format("/employees?search=%s", search))
                .exchange()

        then:
        noExceptionThrown()
        responseSpec.expectStatus().is2xxSuccessful();

        and:
        Collection<Employee> body = responseSpec.expectBodyList(Employee).returnResult().responseBody
        body.size() == 1
        body.first() != null

        and:
        Employee employee = body.first()
        employee.firstname() == "Dora"
        employee.lastname() == "Doorbell"
        employee.birthdate() == LocalDate.of(1992,10,20)
    }

    void 'should return empty list when searching for none existing user'() {
        given:
        String search = "firstname:Peter,lastname:Parker"

        when:
        def responseSpec = webTestClient.get()
                .uri(String.format("/employees?search=%s", search))
                .exchange()

        then:
        noExceptionThrown()
        responseSpec.expectStatus().is2xxSuccessful();

        and:
        Collection<Employee> body = responseSpec.expectBodyList(Employee).returnResult().responseBody
        body.size() == 0
    }
}
