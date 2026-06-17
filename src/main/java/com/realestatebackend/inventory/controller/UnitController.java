package com.realestatebackend.inventory.controller;

import com.realestatebackend.inventory.dto.CreateUnitRequest;
import com.realestatebackend.inventory.dto.UnitResponse;
import com.realestatebackend.inventory.entity.UnitStatus;
import com.realestatebackend.inventory.entity.UnitType;
import com.realestatebackend.inventory.service.UnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<UnitResponse> create(@Valid @RequestBody CreateUnitRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','AGENT')")
    public ResponseEntity<UnitResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * Paged listing of a project's units, with optional status/type filters.
     *   GET /api/v1/units?projectId=...&page=0&size=20&sort=unitNumber
     *   GET /api/v1/units?projectId=...&status=AVAILABLE
     *   GET /api/v1/units?projectId=...&type=PLOT
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','AGENT')")
    public ResponseEntity<Page<UnitResponse>> list(
            @RequestParam UUID projectId,
            @RequestParam(required = false) UnitStatus status,
            @RequestParam(required = false) UnitType type,
            Pageable pageable) {

        Page<UnitResponse> result;
        if (status != null) {
            result = service.listByProjectAndStatus(projectId, status, pageable);
        } else if (type != null) {
            result = service.listByProjectAndType(projectId, type, pageable);
        } else {
            result = service.listByProject(projectId, pageable);
        }
        return ResponseEntity.ok(result);
    }

    // Units within a single block (paged)
    @GetMapping("/by-block/{blockId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','AGENT')")
    public ResponseEntity<Page<UnitResponse>> listByBlock(
            @PathVariable UUID blockId, Pageable pageable) {
        return ResponseEntity.ok(service.listByBlock(blockId, pageable));
    }

    /**
     * Change a unit's lifecycle status. AGENT can do this too, because
     * moving a unit to BOOKED etc. is part of the sales workflow.
     *   PATCH /api/v1/units/{id}/status?value=BOOKED
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','AGENT')")
    public ResponseEntity<UnitResponse> changeStatus(
            @PathVariable UUID id, @RequestParam UnitStatus value) {
        return ResponseEntity.ok(service.changeStatus(id, value));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}