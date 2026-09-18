package com.vinci.training_facility.service.impl;

import com.vinci.training_facility.dto.request.ParticipantRequest;
import com.vinci.training_facility.dto.request.CoachRequest;
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
import com.vinci.training_facility.repository.specification.RegistrationSpecification;
import com.vinci.training_facility.repository.specification.SessionSpecification;
import com.vinci.training_facility.service.TrainingFacilityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class TrainingFacilityServiceImpl implements TrainingFacilityService {

    private final ParticipantRepository participantRepository;
    private final CoachRepository coachRepository;
    private final SessionRepository sessionRepository;
    private final RegistrationRepository registrationRepository;
    private final SessionMapper sessionMapper;
    private final RegistrationMapper registrationMapper;

    public TrainingFacilityServiceImpl(ParticipantRepository participantRepository,
                                       CoachRepository coachRepository,
                                       SessionRepository sessionRepository,
                                       RegistrationRepository registrationRepository,
                                       SessionMapper sessionMapper, RegistrationMapper registrationMapper) {
        this.participantRepository = participantRepository;
        this.coachRepository = coachRepository;
        this.sessionRepository = sessionRepository;
        this.registrationRepository = registrationRepository;
        this.sessionMapper = sessionMapper;
        this.registrationMapper = registrationMapper;
    }

    public void createParticipant(ParticipantRequest participantRequest) {
        Participant newParticipant = new Participant();
        newParticipant.setName(participantRequest.getName());
        newParticipant.setEmail(participantRequest.getEmail());
        participantRepository.save(newParticipant);
    }

    public void createCoach(CoachRequest coachRequest) {
            Coach newCoach = new Coach();
            newCoach.setName(coachRequest.getName());
            newCoach.setEmail(coachRequest.getEmail());
            coachRepository.save(newCoach);
    }

    public void createSession(SessionRequest sessionRequest) {
        Session newSession = new Session();

        ZoneId zone = ZoneId.of("America/Argentina/Buenos_Aires");

        Instant start = sessionRequest.getStartTime().atStartOfDay(zone).toInstant();
        Instant end = sessionRequest.getEndTime().atStartOfDay(zone).toInstant();

        Specification<Session> spec = SessionSpecification.hasCoach(sessionRequest.getCoachId())
                .and(SessionSpecification.hasStartTime(start))
                .and(SessionSpecification.hasEndTime(end));

        Optional<Session> existingSession = sessionRepository.findOne(spec);

        if(existingSession.isPresent()){
            throw new ConflictException("Session already exists for this coach in the given time range");
        }

        newSession.setCoachId(sessionRequest.getCoachId());
        newSession.setStartTime(start);
        newSession.setEndTime(end);
        newSession.setCapacity(sessionRequest.getCapacity());
        newSession.setLocation(sessionRequest.getLocation());
        newSession.setStatus(sessionRequest.getStatus());
        sessionRepository.save(newSession);
    }


    public Page<SessionResponse> listSessionsByCoachAndDateRange(Long coachId, Instant startDate, Instant endDate, Pageable pageable) {
        Specification<Session> spec = SessionSpecification.hasCoach(coachId)
                .and(SessionSpecification.hasStartTime(startDate)
                        .and(SessionSpecification.hasEndTime(endDate)));

        return sessionRepository.findAll(spec, pageable)
                .map(sessionMapper::toResponse);

    }

    public void cancelSession(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        session.setStatus(SessionStatus.INACTIVE);
        sessionRepository.save(session);
    }


    public void registerParticipantToSession(Long participantId, Long sessionId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        if(session.getCapacity() <= 1) {
            throw new ConflictException("Session is full");
        }

        Registration registration = new Registration();
        Optional<Registration> existsAlreadyInSession = registrationRepository
                .findOne(RegistrationSpecification.isParticipantInSession(participantId, sessionId));

        if(existsAlreadyInSession.isPresent()) {
            throw new ConflictException("Participant is already registered for this session");
        }

        registration.setParticipant(participant);
        registration.setSession(session);
        registration.setStatus(RegistrationStatus.CONFIRMED);

        registrationRepository.save(registration);
    }

    @Transactional(readOnly = true)
    public Page<ParticipantResponse> getParticipantsBySessionId(Long sessionId, Pageable pageable) {
        sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        Specification<Registration> spec = RegistrationSpecification.hasParticipantBySession(sessionId);

        Page<Registration> registrations = registrationRepository.findAll(spec, pageable);

        return registrations.map(registrationMapper::toParticipantResponse);
    }


    public void cancelRegistration(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

        registration.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);

    }
}
