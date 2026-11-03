package com.example.servigohex.infrastructure.adapters.in.rest.mappers;

import com.example.servigohex.domain.models.ServiceCategory;
import com.example.servigohex.infrastructure.adapters.in.rest.dto.ServiceCategoryRequest;
import com.example.servigohex.infrastructure.adapters.in.rest.dto.ServiceCategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceCategoryMapper {

    ServiceCategoryMapper INSTANCE = Mappers.getMapper(ServiceCategoryMapper.class);

    ServiceCategoryResponse toServiceCategoryResponse(ServiceCategory categoria);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    ServiceCategory toServiceCategory(ServiceCategoryRequest request);
}
