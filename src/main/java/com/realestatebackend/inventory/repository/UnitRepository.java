package com.realestatebackend.inventory.repository;

import com.realestatebackend.inventory.entity.Unit;
import com.realestatebackend.inventory.entity.UnitStatus;
import com.realestatebackend.inventory.entity.UnitType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Data access for Unit — the entity we search, filter, page, and count most.
 */
public interface UnitRepository extends JpaRepository<Unit, UUID> {

    // --- listing (paged) ---

    // All units in a given block, paged.
    Page<Unit> findByBlock_Id(UUID blockId, Pageable pageable);

    // All units across a whole project (any block), paged.
    // Deep traversal: Unit -> block -> project -> id
    Page<Unit> findByBlock_Project_Id(UUID projectId, Pageable pageable);

    // Filter a project's units by status, paged (e.g. show only AVAILABLE).
    Page<Unit> findByBlock_Project_IdAndStatus(UUID projectId, UnitStatus status, Pageable pageable);

    // Filter a project's units by type, paged (e.g. show only PLOT).
    Page<Unit> findByBlock_Project_IdAndUnitType(UUID projectId, UnitType unitType, Pageable pageable);

    // --- counting (for dashboards / reports) ---

    long countByBlock_Project_IdAndStatus(UUID projectId, UnitStatus status);

    long countByBlock_Project_Id(UUID projectId);

    // --- guards ---

    // No two units with the same number inside the same block.
    boolean existsByBlock_IdAndUnitNumberIgnoreCase(UUID blockId, String unitNumber);
}