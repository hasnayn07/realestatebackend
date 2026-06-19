package com.realestatebackend.dealer.controller;

import com.realestatebackend.dealer.dto.DealerPayableDto;
import com.realestatebackend.dealer.dto.DealerRequest;
import com.realestatebackend.dealer.dto.DealerResponse;
import com.realestatebackend.dealer.service.DealerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dealers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class DealerController {

    private final DealerService dealerService;

    @PostMapping
    public ResponseEntity<DealerResponse> createDealer(@Valid @RequestBody DealerRequest request) {
        DealerResponse response = dealerService.createDealer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DealerResponse>> getAllDealers() {
        return ResponseEntity.ok(dealerService.getAllDealers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DealerResponse> getDealerById(@PathVariable UUID id) {
        return ResponseEntity.ok(dealerService.getDealerById(id));
    }

    @GetMapping("/payables")
    public ResponseEntity<List<DealerPayableDto>> getDealerPayables() {
        return ResponseEntity.ok(dealerService.getDealerPayables());
    }
}