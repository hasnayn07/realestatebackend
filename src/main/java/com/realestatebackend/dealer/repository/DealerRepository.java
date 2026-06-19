package com.realestatebackend.dealer.repository;

import com.realestatebackend.dealer.entity.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DealerRepository extends JpaRepository<Dealer, UUID> {
    boolean existsByCnic(String cnic);
}