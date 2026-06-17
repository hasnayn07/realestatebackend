package com.realestatebackend.booking.service.impl;

import com.realestatebackend.booking.dto.CreateCustomerRequest;
import com.realestatebackend.booking.dto.CustomerResponse;
import com.realestatebackend.booking.entity.Customer;
import com.realestatebackend.booking.repository.CustomerRepository;
import com.realestatebackend.booking.service.CustomerService;
import com.realestatebackend.common.exception.BadRequestException;
import com.realestatebackend.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepo;

    @Override
    @Transactional
    public CustomerResponse create(CreateCustomerRequest req) {
        if (customerRepo.existsByCnic(req.cnic())) {
            throw new BadRequestException("A customer with CNIC " + req.cnic() + " already exists");
        }
        Customer c = Customer.builder()
                .fullName(req.fullName())
                .cnic(req.cnic())
                .phone(req.phone())
                .address(req.address())
                .build();
        return toResponse(customerRepo.save(c));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAll() {
        return customerRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        customerRepo.delete(findOrThrow(id));
    }

    private Customer findOrThrow(UUID id) {
        return customerRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
    }

    private CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(
                c.getId(), c.getFullName(), c.getCnic(),
                c.getPhone(), c.getAddress(), c.getCreatedAt());
    }
}