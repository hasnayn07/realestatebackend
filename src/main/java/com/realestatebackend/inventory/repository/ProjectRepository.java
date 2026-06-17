package com.realestatebackend.inventory.repository;

import com.realestatebackend.inventory.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Data access for Project. Extending JpaRepository<Project, UUID> gives us
 * save / findById / findAll / delete / paging for free.
 */
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    // Derived query: SELECT * FROM projects WHERE LOWER(city) = LOWER(?)
    List<Project> findByCityIgnoreCase(String city);

    // Guard against duplicate project names when creating
    boolean existsByNameIgnoreCase(String name);
}