package com.vinci.training_facility.controller;

import com.vinci.training_facility.dto.request.CoachRequest;
import com.vinci.training_facility.dto.request.ParticipantRequest;
import com.vinci.training_facility.dto.request.SessionRequest;
import com.vinci.training_facility.dto.response.ParticipantResponse;
import com.vinci.training_facility.dto.response.SessionResponse;
import com.vinci.training_facility.service.TrainingFacilityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/v1/training-facility")
public class TrainingFacilityController {

    private final TrainingFacilityService trainingFacilityService;

    public TrainingFacilityController(TrainingFacilityService trainingFacilityService) {
        this.trainingFacilityService = trainingFacilityService;
    }

    @PostMapping("/create-participant")
    public ResponseEntity<String> createParticipant(@RequestBody ParticipantRequest participantRequest){
        try {
            trainingFacilityService.createParticipant(participantRequest);
            return new ResponseEntity<>("Participant created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating participant: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-coach")
    public ResponseEntity<String> createCoach(@RequestBody CoachRequest coachRequest){
        try {
            trainingFacilityService.createCoach(coachRequest);
            return new ResponseEntity<>("Coach created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating coach: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-session")
    public ResponseEntity<String> createSession(@RequestBody SessionRequest sessionRequest){
        try {
            trainingFacilityService.createSession(sessionRequest);
            return new ResponseEntity<>("Session created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating session: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sessions")
    public ResponseEntity<Page<SessionResponse>> listSessionsByCoachAndDateRange(@RequestParam("id") Long coachId, @RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate, Pageable pageable){
        try {
            ZoneId zone = ZoneId.of("America/Argentina/Buenos_Aires");

            Instant start = startDate.atStartOfDay(zone).toInstant();
            Instant end = endDate.plusDays(1).atStartOfDay(zone).toInstant();

            Page<SessionResponse> sessionResponse = trainingFacilityService.listSessionsByCoachAndDateRange(coachId, start, end, pageable);
            return new ResponseEntity<>(sessionResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register-participant")
    public ResponseEntity<String> registerParticipantToSession(@RequestParam("participantId") Long participantId, @RequestParam("sessionId") Long sessionId){
        try {
            trainingFacilityService.registerParticipantToSession(participantId, sessionId);
            return new ResponseEntity<>("Participant registered to session successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error registering participant to session: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/participants/{sessionId}")
    public ResponseEntity<Page<ParticipantResponse>> getParticipantsBySessionId(@PathVariable Long sessionId, Pageable pageable){
        return new ResponseEntity<>(trainingFacilityService
                .getParticipantsBySessionId(sessionId, pageable), HttpStatus.OK);
    }

    @PutMapping("/cancel-registration/{registrationId}")
    public ResponseEntity<String> cancelRegistration(@PathVariable Long registrationId){
        try {
            trainingFacilityService.cancelRegistration(registrationId);
            return new ResponseEntity<>("Registration cancelled successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error cancelling registration: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/cancel-session/{sessionId}")
    public ResponseEntity<String> cancelSession(@PathVariable Long sessionId){
        try {
            trainingFacilityService.cancelSession(sessionId);
            return new ResponseEntity<>("Session cancelled successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error cancelling session: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
