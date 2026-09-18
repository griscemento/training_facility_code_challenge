package com.vinci.training_facility.repository.specification;

import com.vinci.training_facility.model.Registration;
import org.springframework.data.jpa.domain.Specification;

public class RegistrationSpecification {

    public static Specification<Registration> hasParticipantBySession(Long sessionId) {
        return (root, query, criteriaBuilder) -> {
            if(sessionId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("session").get("id"), sessionId);
        };
    }

    public static Specification<Registration> isParticipantInSession(Long participantId, Long sessionId) {
        return (root, query, criteriaBuilder) -> {
            if(participantId == null || sessionId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("participant").get("id"), participantId),
                    criteriaBuilder.equal(root.get("session").get("id"), sessionId)
            );
        };
    }
}
