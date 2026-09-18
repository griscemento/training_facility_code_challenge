package com.vinci.training_facility.repository;

import com.vinci.training_facility.model.Participant;
import com.vinci.training_facility.model.Registration;
import com.vinci.training_facility.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration,Long>,
        JpaSpecificationExecutor<Registration> {
}
