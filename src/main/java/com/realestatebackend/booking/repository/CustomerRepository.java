package com.realestatebackend.booking.repository;

import com.realestatebackend.booking.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByCnic(String cnic);

    boolean existsByCnic(String cnic);
}