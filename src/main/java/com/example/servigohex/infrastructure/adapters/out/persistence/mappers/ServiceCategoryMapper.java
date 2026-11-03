package com.example.servigohex.infrastructure.adapters.out.persistence.mappers;

import com.example.servigohex.domain.models.ServiceCategory;
import com.example.servigohex.infrastructure.adapters.out.persistence.entities.ServiceCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceCategoryMapper {

    ServiceCategoryMapper INSTANCE = Mappers.getMapper(ServiceCategoryMapper.class);

    ServiceCategoryEntity toEntity(ServiceCategory categoria);

    ServiceCategory toServiceCategory(ServiceCategoryEntity entity);
}
