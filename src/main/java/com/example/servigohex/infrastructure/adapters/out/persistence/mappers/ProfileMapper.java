package com.example.servigohex.infrastructure.adapters.out.persistence.mappers;

import com.example.servigohex.domain.models.Profile;
import com.example.servigohex.infrastructure.adapters.out.persistence.entities.ProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProfileMapper {

    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

    ProfileEntity toEntity(Profile profile);

    Profile toProfile(ProfileEntity entity);
}
