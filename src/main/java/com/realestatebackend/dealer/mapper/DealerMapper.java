package com.realestatebackend.dealer.mapper;

import com.realestatebackend.dealer.dto.DealerRequest;
import com.realestatebackend.dealer.dto.DealerResponse;
import com.realestatebackend.dealer.entity.Dealer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DealerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Dealer toEntity(DealerRequest request);

    DealerResponse toResponse(Dealer dealer);
}