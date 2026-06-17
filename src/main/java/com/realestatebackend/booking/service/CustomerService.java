package com.realestatebackend.booking.service;

import com.realestatebackend.booking.dto.CreateCustomerRequest;
import com.realestatebackend.booking.dto.CustomerResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerResponse create(CreateCustomerRequest req);
    CustomerResponse getById(UUID id);
    List<CustomerResponse> getAll();
    void delete(UUID id);
}