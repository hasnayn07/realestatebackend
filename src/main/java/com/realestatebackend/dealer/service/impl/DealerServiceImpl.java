package com.realestatebackend.dealer.service.impl;

import com.realestatebackend.booking.repository.BookingRepository;
import com.realestatebackend.common.exception.BadRequestException;
import com.realestatebackend.common.exception.NotFoundException;
import com.realestatebackend.dealer.dto.DealerPayableDto;
import com.realestatebackend.dealer.dto.DealerRequest;
import com.realestatebackend.dealer.dto.DealerResponse;
import com.realestatebackend.dealer.entity.Dealer;
import com.realestatebackend.dealer.mapper.DealerMapper;
import com.realestatebackend.dealer.repository.DealerRepository;
import com.realestatebackend.dealer.service.DealerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealerServiceImpl implements DealerService {

    private final DealerRepository dealerRepository;
    private final BookingRepository bookingRepository;
    private final DealerMapper dealerMapper;

    @Override
    @Transactional
    public DealerResponse createDealer(DealerRequest request) {
        if (dealerRepository.existsByCnic(request.cnic())) {
            throw new BadRequestException("A dealer with this CNIC already exists.");
        }

        Dealer dealer = dealerMapper.toEntity(request);
        dealer = dealerRepository.save(dealer);

        log.info("Created new dealer: {} with CNIC: {}", dealer.getName(), dealer.getCnic());
        return dealerMapper.toResponse(dealer);
    }

    @Override
    @Transactional(readOnly = true)
    public DealerResponse getDealerById(UUID id) {
        Dealer dealer = dealerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dealer not found with ID: " + id));
        return dealerMapper.toResponse(dealer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DealerResponse> getAllDealers() {
        return dealerRepository.findAll().stream()
                .map(dealerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DealerPayableDto> getDealerPayables() {
        // 1. Fetch the raw aggregates (total bookings, total sales volume) from the database
        List<DealerPayableDto> rawAggregates = bookingRepository.getDealerSalesAggregates();

        if (rawAggregates.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. Extract IDs and fetch the corresponding Dealer entities in ONE query to avoid N+1 issue
        List<UUID> dealerIds = rawAggregates.stream()
                .map(DealerPayableDto::dealerId)
                .toList();

        Map<UUID, Dealer> dealerMap = dealerRepository.findAllById(dealerIds).stream()
                .collect(Collectors.toMap(Dealer::getId, dealer -> dealer));

        // 3. Apply the strict exact commission math to each aggregate
        List<DealerPayableDto> finalPayables = new ArrayList<>();

        for (DealerPayableDto raw : rawAggregates) {
            Dealer dealer = dealerMap.get(raw.dealerId());
            if (dealer == null) continue;

            BigDecimal commissionPercentage = dealer.getCommissionPercentage();

            // Math: (totalSalesVolume * commissionPercentage) / 100
            BigDecimal calculatedCommission = raw.totalSalesVolume()
                    .multiply(commissionPercentage)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            // Rebuild the DTO with the final calculated commission
            finalPayables.add(new DealerPayableDto(
                    raw.dealerId(),
                    raw.dealerName(),
                    raw.agencyName(),
                    raw.totalBookings(),
                    raw.totalSalesVolume(),
                    calculatedCommission
            ));
        }

        return finalPayables;
    }
}