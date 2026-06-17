package com.realestatebackend.inventory.service.impl;

import com.realestatebackend.common.exception.BadRequestException;
import com.realestatebackend.common.exception.NotFoundException;
import com.realestatebackend.inventory.dto.CreateProjectRequest;
import com.realestatebackend.inventory.dto.ProjectResponse;
import com.realestatebackend.inventory.entity.Project;
import com.realestatebackend.inventory.repository.ProjectRepository;
import com.realestatebackend.inventory.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepo;

    @Override
    @Transactional
    public ProjectResponse create(CreateProjectRequest req) {
        // Guard: reject duplicate project names (clean 400 instead of a DB error)
        if (projectRepo.existsByNameIgnoreCase(req.name())) {
            throw new BadRequestException("A project named '" + req.name() + "' already exists");
        }

        Project project = Project.builder()
                .name(req.name())
                .city(req.city())
                .description(req.description())
                .build();

        return toResponse(projectRepo.save(project));
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAll() {
        return projectRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Project project = findOrThrow(id);
        projectRepo.delete(project);
    }

    // --- helpers ---

    private Project findOrThrow(UUID id) {
        return projectRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Project not found: " + id));
    }

    private ProjectResponse toResponse(Project p) {
        return new ProjectResponse(
                p.getId(), p.getName(), p.getCity(), p.getDescription(), p.getCreatedAt()
        );
    }
}