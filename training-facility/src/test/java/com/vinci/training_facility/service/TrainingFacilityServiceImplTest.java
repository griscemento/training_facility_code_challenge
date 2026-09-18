package com.vinci.training_facility.service;

import com.vinci.training_facility.dto.request.CoachRequest;
import com.vinci.training_facility.dto.request.ParticipantRequest;
import com.vinci.training_facility.dto.request.SessionRequest;
import com.vinci.training_facility.dto.response.ParticipantResponse;
import com.vinci.training_facility.dto.response.SessionResponse;
import com.vinci.training_facility.exception.ConflictException;
import com.vinci.training_facility.exception.ResourceNotFoundException;
import com.vinci.training_facility.mapper.RegistrationMapper;
import com.vinci.training_facility.mapper.SessionMapper;
import com.vinci.training_facility.model.*;
import com.vinci.training_facility.model.enums.RegistrationStatus;
import com.vinci.training_facility.model.enums.SessionStatus;
import com.vinci.training_facility.repository.CoachRepository;
import com.vinci.training_facility.repository.ParticipantRepository;
import com.vinci.training_facility.repository.RegistrationRepository;
import com.vinci.training_facility.repository.SessionRepository;
import com.vinci.training_facility.service.impl.TrainingFacilityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingFacilityServiceImplTest {

    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private CoachRepository coachRepository;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private SessionMapper sessionMapper;
    @Mock
    private RegistrationRepository registrationRepository;
    @Mock
    private RegistrationMapper registrationMapper;

    @InjectMocks
    private TrainingFacilityServiceImpl service;

    @Captor
    private ArgumentCaptor<Participant> participantCaptor;

    @Captor
    private ArgumentCaptor<Coach> coachCaptor;

    @Captor
    private ArgumentCaptor<Session> sessionCaptor;

    @Captor
    private ArgumentCaptor<Registration> registrationCaptor;

    @BeforeEach
    void setUp() {}

    @Test
    void createParticipant_savesParticipant() {
        ParticipantRequest req = new ParticipantRequest("John Doe", "john@example.com");

        service.createParticipant(req);

        verify(participantRepository).save(participantCaptor.capture());
        Participant saved = participantCaptor.getValue();
        assertThat(saved.getName()).isEqualTo("John Doe");
        assertThat(saved.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void createCoach_savesCoach() {
        CoachRequest req = new CoachRequest("Coach A", "coach@example.com");

        service.createCoach(req);

        verify(coachRepository).save(coachCaptor.capture());
        Coach saved = coachCaptor.getValue();
        assertThat(saved.getName()).isEqualTo("Coach A");
        assertThat(saved.getEmail()).isEqualTo("coach@example.com");
    }

    @Test
    void createSession_savesWhenNoExisting() {

        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plus(1, java.time.temporal.ChronoUnit.DAYS);

        SessionRequest req = new SessionRequest(1L, start, end, 10L, "Room", SessionStatus.ACTIVE);

        when(sessionRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        service.createSession(req);

        verify(sessionRepository).save(sessionCaptor.capture());
        Session saved = sessionCaptor.getValue();
        assertThat(saved.getCoachId()).isEqualTo(1L);
        assertThat(saved.getLocation()).isEqualTo("Room");
        assertThat(saved.getCapacity()).isEqualTo(10L);
    }

    @Test
    void createSession_throwsOnConflict() {
        SessionRequest req = new SessionRequest();

        when(sessionRepository.findOne(any(Specification.class))).thenReturn(Optional.of(new Session()));

        assertThrows(ConflictException.class, () -> service.createSession(req));
    }

   @Test
    void listSessionsByCoachAndDateRange_returnsResponse() {
        Long coachId = 1L;
        Instant start = Instant.now();
        Instant end = Instant.now().plus(1, java.time.temporal.ChronoUnit.DAYS);

        Session session = new Session();
        session.setId(5L);
        session.setCoachId(coachId);
        session.setStartTime(start);
        session.setEndTime(end);

        when(sessionRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(session)));

        SessionResponse resp = new SessionResponse();
        resp.setId(5L);
        when(sessionMapper.toResponse(session)).thenReturn(resp);

        Page<SessionResponse> result = service.listSessionsByCoachAndDateRange(coachId, start, end, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(5L);
    }

    @Test
    void listSessionsByCoachAndDateRange_returnsEmptyPageWhenNoSessions() {
        Long coachId = 1L;
        Instant start = Instant.now();
        Instant end = Instant.now().plus(1, java.time.temporal.ChronoUnit.DAYS);

        when(sessionRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<SessionResponse> result = service.listSessionsByCoachAndDateRange(coachId, start, end, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    void registerParticipantToSession_savesRegistration() {
        Participant participant = new Participant(); participant.setId(1L);
        Session session = new Session(); session.setId(2L); session.setCapacity(10L);

        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant));
        when(sessionRepository.findById(2L)).thenReturn(Optional.of(session));
        when(registrationRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        service.registerParticipantToSession(1L, 2L);

        verify(registrationRepository).save(registrationCaptor.capture());
        Registration saved = registrationCaptor.getValue();
        assertThat(saved.getParticipant()).isEqualTo(participant);
        assertThat(saved.getSession()).isEqualTo(session);
    }

    @Test
    void registerParticipantToSession_throwsWhenParticipantMissing() {
        when(participantRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.registerParticipantToSession(1L, 2L));
    }

    @Test
    void registerParticipantToSession_throwsWhenAlreadyRegistered() {
        Participant participant = new Participant(); participant.setId(1L);
        Session session = new Session(); session.setId(2L); session.setCapacity(10L);
        Registration existing = new Registration();

        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant));
        when(sessionRepository.findById(2L)).thenReturn(Optional.of(session));
        when(registrationRepository.findOne(any(Specification.class))).thenReturn(Optional.of(existing));

        assertThrows(ConflictException.class, () -> service.registerParticipantToSession(1L, 2L));
    }

    @Test
    void getParticipantsBySessionId_returnsPage() {
        Long sessionId = 2L;
        Pageable pageable = PageRequest.of(0, 10);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(new Session()));

        Registration r1 = new Registration();
        Registration r2 = new Registration();
        Page<Registration> page = new PageImpl<>(List.of(r1, r2));

        when(registrationRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        ParticipantResponse pResp = new ParticipantResponse();
        when(registrationMapper.toParticipantResponse(any(Registration.class))).thenReturn(pResp);

        Page<ParticipantResponse> result = service.getParticipantsBySessionId(sessionId, pageable);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void getParticipantsBySessionId_throwsWhenSessionMissing() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getParticipantsBySessionId(1L, PageRequest.of(0, 10)));
    }

    @Test
    void cancelRegistration_setsCancelled() {
        Registration reg = new Registration(); reg.setId(9L); reg.setStatus(RegistrationStatus.CONFIRMED);

        when(registrationRepository.findById(9L)).thenReturn(Optional.of(reg));

        service.cancelRegistration(9L);

        verify(registrationRepository).save(registrationCaptor.capture());
        Registration saved = registrationCaptor.getValue();
        assertThat(saved.getStatus()).isEqualTo(RegistrationStatus.CANCELLED);
    }

    @Test
    void cancelRegistration_throwsWhenMissing() {
        when(registrationRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.cancelRegistration(10L));
    }
}
