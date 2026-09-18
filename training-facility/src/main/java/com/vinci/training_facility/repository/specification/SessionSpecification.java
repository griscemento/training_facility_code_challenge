package com.vinci.training_facility.repository.specification;

import com.vinci.training_facility.model.Session;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;

public class SessionSpecification {

    public static Specification<Session> hasCoach(Long coachId) {
        return (root, query, criteriaBuilder) -> {
            if(coachId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("coachId"), coachId);
        };
    }

    public static Specification<Session> hasStartTime(Instant startDate) {
        return (root, query, criteriaBuilder) -> {
            if(startDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("startTime"), startDate);
        };
    }

    public static Specification<Session> hasEndTime(Instant endDate) {
        return (root, query, criteriaBuilder) -> {
            if(endDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("endTime"), endDate);
        };
    }
}
