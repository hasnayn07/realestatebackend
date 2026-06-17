package com.realestatebackend.inventory.controller;

import com.realestatebackend.inventory.dto.BlockResponse;
import com.realestatebackend.inventory.dto.CreateBlockRequest;
import com.realestatebackend.inventory.service.BlockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/blocks")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<BlockResponse> create(@Valid @RequestBody CreateBlockRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','AGENT')")
    public ResponseEntity<BlockResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // GET /api/v1/blocks?projectId=...
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','AGENT')")
    public ResponseEntity<List<BlockResponse>> getByProject(@RequestParam UUID projectId) {
        return ResponseEntity.ok(service.getByProject(projectId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}