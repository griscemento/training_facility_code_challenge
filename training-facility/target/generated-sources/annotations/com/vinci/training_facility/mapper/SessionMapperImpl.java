package com.vinci.training_facility.mapper;

import com.vinci.training_facility.dto.response.SessionResponse;
import com.vinci.training_facility.model.Session;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-18T10:41:11-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Amazon.com Inc.)"
)
@Component
public class SessionMapperImpl implements SessionMapper {

    @Override
    public SessionResponse toResponse(Session entity) {
        if ( entity == null ) {
            return null;
        }

        SessionResponse sessionResponse = new SessionResponse();

        sessionResponse.setId( entity.getId() );
        sessionResponse.setCoachId( entity.getCoachId() );
        sessionResponse.setStartTime( entity.getStartTime() );
        sessionResponse.setEndTime( entity.getEndTime() );
        sessionResponse.setCapacity( entity.getCapacity() );
        sessionResponse.setLocation( entity.getLocation() );

        return sessionResponse;
    }
}
