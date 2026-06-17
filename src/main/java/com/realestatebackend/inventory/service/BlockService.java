package com.realestatebackend.inventory.service;

import com.realestatebackend.inventory.dto.BlockResponse;
import com.realestatebackend.inventory.dto.CreateBlockRequest;

import java.util.List;
import java.util.UUID;

public interface BlockService {
    BlockResponse create(CreateBlockRequest req);
    BlockResponse getById(UUID id);
    List<BlockResponse> getByProject(UUID projectId);
    void delete(UUID id);
}