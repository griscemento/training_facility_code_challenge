package com.vinci.training_facility.service;

import com.vinci.training_facility.dto.request.ParticipantRequest;
import com.vinci.training_facility.dto.request.CoachRequest;
import com.vinci.training_facility.dto.request.SessionRequest;
import com.vinci.training_facility.dto.response.ParticipantResponse;
import com.vinci.training_facility.dto.response.SessionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;

public interface TrainingFacilityService {

    void createParticipant(ParticipantRequest participantRequest);

    void createCoach(CoachRequest coachRequest);

    void createSession(SessionRequest sessionRequest);

    Page<SessionResponse> listSessionsByCoachAndDateRange(Long coachId, Instant startDate, Instant endDate, Pageable pageable);

    void registerParticipantToSession(Long participantId, Long sessionId);

    Page<ParticipantResponse> getParticipantsBySessionId(Long sessionId, Pageable pageable);

    void cancelRegistration(Long registrationId);

    void cancelSession(Long sessionId);

}
