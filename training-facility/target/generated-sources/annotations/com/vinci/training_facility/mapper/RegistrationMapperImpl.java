package com.vinci.training_facility.mapper;

import com.vinci.training_facility.dto.response.ParticipantResponse;
import com.vinci.training_facility.dto.response.RegistrationResponse;
import com.vinci.training_facility.model.Participant;
import com.vinci.training_facility.model.Registration;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-18T10:41:11-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Amazon.com Inc.)"
)
@Component
public class RegistrationMapperImpl implements RegistrationMapper {

    @Override
    public RegistrationResponse toResponse(Registration entity) {
        if ( entity == null ) {
            return null;
        }

        RegistrationResponse registrationResponse = new RegistrationResponse();

        registrationResponse.setId( entity.getId() );
        registrationResponse.setParticipant( entity.getParticipant() );
        registrationResponse.setSession( entity.getSession() );
        registrationResponse.setRegisteredAt( entity.getRegisteredAt() );
        if ( entity.getStatus() != null ) {
            registrationResponse.setStatus( entity.getStatus().name() );
        }

        return registrationResponse;
    }

    @Override
    public ParticipantResponse toParticipantResponse(Registration registrationPartialEntity) {
        if ( registrationPartialEntity == null ) {
            return null;
        }

        ParticipantResponse participantResponse = new ParticipantResponse();

        participantResponse.setId( registrationPartialEntityParticipantId( registrationPartialEntity ) );
        participantResponse.setName( registrationPartialEntityParticipantName( registrationPartialEntity ) );
        participantResponse.setEmail( registrationPartialEntityParticipantEmail( registrationPartialEntity ) );

        return participantResponse;
    }

    private Long registrationPartialEntityParticipantId(Registration registration) {
        if ( registration == null ) {
            return null;
        }
        Participant participant = registration.getParticipant();
        if ( participant == null ) {
            return null;
        }
        Long id = participant.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String registrationPartialEntityParticipantName(Registration registration) {
        if ( registration == null ) {
            return null;
        }
        Participant participant = registration.getParticipant();
        if ( participant == null ) {
            return null;
        }
        String name = participant.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String registrationPartialEntityParticipantEmail(Registration registration) {
        if ( registration == null ) {
            return null;
        }
        Participant participant = registration.getParticipant();
        if ( participant == null ) {
            return null;
        }
        String email = participant.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }
}
