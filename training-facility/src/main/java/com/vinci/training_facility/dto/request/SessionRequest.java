package com.vinci.training_facility.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vinci.training_facility.model.enums.SessionStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionRequest {

    private Long coachId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate endTime;
    private Long capacity;
    private String location;
    @Enumerated(EnumType.STRING)
    private SessionStatus status;
}
