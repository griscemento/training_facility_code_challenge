package com.vinci.training_facility.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vinci.training_facility.model.enums.SessionStatus;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
public class SessionResponse {

    private Long id;
    private Long coachId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Instant startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Instant endTime;
    private Long capacity;
    private String location;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private SessionStatus sessionStatus;
}
