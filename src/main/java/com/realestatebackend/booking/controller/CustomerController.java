package com.realestatebackend.booking.controller;

import com.realestatebackend.booking.dto.CreateCustomerRequest;
import com.realestatebackend.booking.dto.CustomerResponse;
import com.realestatebackend.booking.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','AGENT')")
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','AGENT')")
    public ResponseEntity<CustomerResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','AGENT')")
    public ResponseEntity<List<CustomerResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}