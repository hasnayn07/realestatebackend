package com.realestatebackend.dealer.service;

import com.realestatebackend.dealer.dto.DealerPayableDto;
import com.realestatebackend.dealer.dto.DealerRequest;
import com.realestatebackend.dealer.dto.DealerResponse;

import java.util.List;
import java.util.UUID;

public interface DealerService {
    DealerResponse createDealer(DealerRequest request);
    DealerResponse getDealerById(UUID id);
    List<DealerResponse> getAllDealers();
    List<DealerPayableDto> getDealerPayables();
}