package com.vinci.training_facility.dto.response;

import com.vinci.training_facility.model.Participant;
import com.vinci.training_facility.model.Session;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter @Setter
public class RegistrationResponse {

    private Long id;
    private Participant participant;
    private Session session;
    private Instant registeredAt;
    private String status;
}
