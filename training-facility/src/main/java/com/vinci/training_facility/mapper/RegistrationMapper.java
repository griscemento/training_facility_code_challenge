package com.vinci.training_facility.mapper;

import com.vinci.training_facility.dto.response.ParticipantResponse;
import com.vinci.training_facility.dto.response.RegistrationResponse;
import com.vinci.training_facility.model.Registration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {

    RegistrationResponse toResponse(Registration entity);

    @Mapping(source = "participant.id", target = "id")
    @Mapping(source = "participant.name", target = "name")
    @Mapping(source = "participant.email", target = "email")
    ParticipantResponse toParticipantResponse(Registration registrationPartialEntity);
}
