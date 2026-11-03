package com.example.servigohex.infrastructure.adapters.in.rest.mappers;

import com.example.servigohex.domain.models.Profile;
import com.example.servigohex.infrastructure.adapters.in.rest.dto.ProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProfileMapper {

    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

    ProfileResponse toProfileResponse(Profile profile);
}
