package com.vinci.training_facility.repository;

import com.vinci.training_facility.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session,Long>,
        JpaSpecificationExecutor<Session> {
}
