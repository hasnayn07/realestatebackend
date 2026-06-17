package com.realestatebackend.inventory.service.impl;

import com.realestatebackend.common.exception.BadRequestException;
import com.realestatebackend.common.exception.NotFoundException;
import com.realestatebackend.inventory.dto.CreateUnitRequest;
import com.realestatebackend.inventory.dto.UnitResponse;
import com.realestatebackend.inventory.entity.*;
import com.realestatebackend.inventory.repository.BlockRepository;
import com.realestatebackend.inventory.repository.UnitRepository;
import com.realestatebackend.inventory.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepo;
    private final BlockRepository blockRepo;

    @Override
    @Transactional
    public UnitResponse create(CreateUnitRequest req) {
        // Parent block must exist
        Block block = blockRepo.findById(req.blockId())
                .orElseThrow(() -> new NotFoundException("Block not found: " + req.blockId()));

        // No duplicate unit number within the same block
        if (unitRepo.existsByBlock_IdAndUnitNumberIgnoreCase(block.getId(), req.unitNumber())) {
            throw new BadRequestException(
                    "Unit '" + req.unitNumber() + "' already exists in this block");
        }

        Unit unit = Unit.builder()
                .block(block)
                .unitNumber(req.unitNumber())
                .unitType(req.unitType())
                .sizeValue(req.sizeValue())
                .sizeUnit(req.sizeUnit())
                .listedPrice(req.listedPrice())
                .bedrooms(req.bedrooms())
                .bathrooms(req.bathrooms())
                .floor(req.floor())
                .status(UnitStatus.AVAILABLE)   // every new unit starts AVAILABLE
                .build();

        return toResponse(unitRepo.save(unit));
    }

    @Override
    @Transactional(readOnly = true)
    public UnitResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UnitResponse> listByProject(UUID projectId, Pageable pageable) {
        return unitRepo.findByBlock_Project_Id(projectId, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UnitResponse> listByProjectAndStatus(UUID projectId, UnitStatus status, Pageable pageable) {
        return unitRepo.findByBlock_Project_IdAndStatus(projectId, status, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UnitResponse> listByProjectAndType(UUID projectId, UnitType type, Pageable pageable) {
        return unitRepo.findByBlock_Project_IdAndUnitType(projectId, type, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UnitResponse> listByBlock(UUID blockId, Pageable pageable) {
        return unitRepo.findByBlock_Id(blockId, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public UnitResponse changeStatus(UUID unitId, UnitStatus newStatus) {
        Unit unit = findOrThrow(unitId);
        assertTransition(unit.getStatus(), newStatus);   // enforce the state machine
        unit.setStatus(newStatus);                       // dirty checking persists this
        return toResponse(unit);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Unit unit = findOrThrow(id);
        // Don't allow deleting a unit that's mid-sale; cancel it first.
        if (unit.getStatus() == UnitStatus.ON_INSTALLMENTS || unit.getStatus() == UnitStatus.POSSESSION) {
            throw new BadRequestException("Cannot delete a unit that is " + unit.getStatus()
                    + ". Cancel or transfer it first.");
        }
        unitRepo.delete(unit);
    }

    // --- the lifecycle state machine ---

    /**
     * Defines which target statuses are legal from each current status.
     * Anything not listed is rejected with a clean 400.
     */
    private void assertTransition(UnitStatus from, UnitStatus to) {
        if (from == to) {
            throw new BadRequestException("Unit is already " + to);
        }
        Set<UnitStatus> allowed = switch (from) {
            case AVAILABLE       -> Set.of(UnitStatus.BOOKED, UnitStatus.CANCELLED);
            case BOOKED          -> Set.of(UnitStatus.ON_INSTALLMENTS, UnitStatus.AVAILABLE, UnitStatus.CANCELLED);
            case ON_INSTALLMENTS -> Set.of(UnitStatus.POSSESSION, UnitStatus.TRANSFERRED, UnitStatus.CANCELLED);
            case POSSESSION      -> Set.of(UnitStatus.TRANSFERRED);
            case TRANSFERRED     -> Set.of();   // terminal
            case CANCELLED       -> Set.of(UnitStatus.AVAILABLE);  // re-list a cancelled unit
        };
        if (!allowed.contains(to)) {
            throw new BadRequestException("Illegal status change: " + from + " -> " + to);
        }
    }

    // --- helpers ---

    private Unit findOrThrow(UUID id) {
        return unitRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Unit not found: " + id));
    }

    private UnitResponse toResponse(Unit u) {
        Block b = u.getBlock();          // LAZY, read inside the transaction — safe
        Project p = b.getProject();      // LAZY, same
        return new UnitResponse(
                u.getId(), u.getUnitNumber(), u.getUnitType(),
                u.getSizeValue(), u.getSizeUnit(), u.getListedPrice(),
                u.getBedrooms(), u.getBathrooms(), u.getFloor(),
                u.getStatus(), u.getCreatedAt(),
                b.getId(), b.getName(), p.getId(), p.getName()
        );
    }
}