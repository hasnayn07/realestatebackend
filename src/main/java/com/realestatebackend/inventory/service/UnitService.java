package com.realestatebackend.inventory.service;

import com.realestatebackend.inventory.dto.CreateUnitRequest;
import com.realestatebackend.inventory.dto.UnitResponse;
import com.realestatebackend.inventory.entity.UnitStatus;
import com.realestatebackend.inventory.entity.UnitType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UnitService {
    UnitResponse create(CreateUnitRequest req);
    UnitResponse getById(UUID id);

    // listing (paged + optional filters)
    Page<UnitResponse> listByProject(UUID projectId, Pageable pageable);
    Page<UnitResponse> listByProjectAndStatus(UUID projectId, UnitStatus status, Pageable pageable);
    Page<UnitResponse> listByProjectAndType(UUID projectId, UnitType type, Pageable pageable);
    Page<UnitResponse> listByBlock(UUID blockId, Pageable pageable);

    // lifecycle transitions
    UnitResponse changeStatus(UUID unitId, UnitStatus newStatus);

    void delete(UUID id);
}