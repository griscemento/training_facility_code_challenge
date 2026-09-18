package com.vinci.training_facility.integration;

import com.vinci.training_facility.model.Coach;
import com.vinci.training_facility.model.Participant;
import com.vinci.training_facility.model.Session;
import com.vinci.training_facility.repository.CoachRepository;
import com.vinci.training_facility.repository.ParticipantRepository;
import com.vinci.training_facility.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrainingFacilityIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("training_facility")
            .withUsername("postgres")
            .withPassword("postgres");

    @org.junit.jupiter.api.BeforeAll
    static void checkDockerAvailable() {
        boolean dockerAvailable = false;
        try {
            dockerAvailable = org.testcontainers.DockerClientFactory.instance().isDockerAvailable();
        } catch (Throwable ignored) {
        }
        org.junit.jupiter.api.Assumptions.assumeTrue(dockerAvailable, "Docker environment required for Testcontainers tests");
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Test
    void fullRegistrationFlow() {
        Participant p = new Participant();
        p.setName("Test User");
        p.setEmail("test@example.com");
        p = participantRepository.save(p);

        Coach c = new Coach();
        c.setName("Coach One");
        c.setEmail("coach@example.com");
        c = coachRepository.save(c);

        Session s = new Session();
        s.setCoachId(c.getId());
        s.setStartTime(Instant.now());
        s.setEndTime(Instant.now().plus(1, java.time.temporal.ChronoUnit.DAYS));
        s.setCapacity(10L);
        s.setLocation("Gym");
        s.setStatus(null);
        s = sessionRepository.save(s);

        String registerUrl = "http://localhost:" + port + "/api/v1/training-facility/register-participant?participantId=" + p.getId() + "&sessionId=" + s.getId();
        ResponseEntity<String> regResponse = restTemplate.postForEntity(registerUrl, null, String.class);
        assertThat(regResponse.getStatusCode().is2xxSuccessful()).isTrue();

        String participantsUrl = "http://localhost:" + port + "/api/v1/training-facility/participants/" + s.getId() + "?page=0&size=10";
        ResponseEntity<Map> participantsResponse = restTemplate.getForEntity(participantsUrl, Map.class);
        assertThat(participantsResponse.getStatusCode().is2xxSuccessful()).isTrue();

        Map body = participantsResponse.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("content")).isInstanceOf(java.util.List.class);
        java.util.List content = (java.util.List) body.get("content");
        assertThat(content).hasSize(1);
        Map first = (Map) content.get(0);
        assertThat(first.get("email")).isEqualTo("test@example.com");
        assertThat(first.get("name")).isEqualTo("Test User");
    }
}
