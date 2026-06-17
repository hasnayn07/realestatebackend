package com.realestatebackend.inventory.service.impl;

import com.realestatebackend.common.exception.BadRequestException;
import com.realestatebackend.common.exception.NotFoundException;
import com.realestatebackend.inventory.dto.BlockResponse;
import com.realestatebackend.inventory.dto.CreateBlockRequest;
import com.realestatebackend.inventory.entity.Block;
import com.realestatebackend.inventory.entity.Project;
import com.realestatebackend.inventory.repository.BlockRepository;
import com.realestatebackend.inventory.repository.ProjectRepository;
import com.realestatebackend.inventory.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService {

    private final BlockRepository blockRepo;
    private final ProjectRepository projectRepo;

    @Override
    @Transactional
    public BlockResponse create(CreateBlockRequest req) {
        // Parent must exist
        Project project = projectRepo.findById(req.projectId())
                .orElseThrow(() -> new NotFoundException("Project not found: " + req.projectId()));

        // No duplicate block name within the same project
        if (blockRepo.existsByProject_IdAndNameIgnoreCase(project.getId(), req.name())) {
            throw new BadRequestException(
                    "Block '" + req.name() + "' already exists in this project");
        }

        Block block = Block.builder()
                .project(project)
                .name(req.name())
                .build();

        return toResponse(blockRepo.save(block));
    }

    @Override
    @Transactional(readOnly = true)
    public BlockResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlockResponse> getByProject(UUID projectId) {
        return blockRepo.findByProject_Id(projectId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        blockRepo.delete(findOrThrow(id));
    }

    private Block findOrThrow(UUID id) {
        return blockRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Block not found: " + id));
    }

    private BlockResponse toResponse(Block b) {
        // b.getProject() is LAZY, but we're inside a transaction, so reading it here is safe.
        return new BlockResponse(
                b.getId(), b.getName(), b.getProject().getId(), b.getProject().getName()
        );
    }
}