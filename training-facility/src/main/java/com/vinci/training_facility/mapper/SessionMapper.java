package com.vinci.training_facility.mapper;

import com.vinci.training_facility.dto.response.SessionResponse;
import com.vinci.training_facility.model.Session;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    SessionResponse toResponse(Session entity);
}
