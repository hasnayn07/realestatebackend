package com.realestatebackend.inventory.service;

import com.realestatebackend.inventory.dto.CreateProjectRequest;
import com.realestatebackend.inventory.dto.ProjectResponse;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    ProjectResponse create(CreateProjectRequest req);
    ProjectResponse getById(UUID id);
    List<ProjectResponse> getAll();
    void delete(UUID id);
}