package com.realestatebackend.inventory.repository;

import com.realestatebackend.inventory.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Data access for Block. Blocks are always queried within a project.
 */
public interface BlockRepository extends JpaRepository<Block, UUID> {

    // All blocks belonging to one project.
    // Property traversal: Block -> project -> id
    List<Block> findByProject_Id(UUID projectId);

    // Prevent two blocks with the same name inside the same project
    boolean existsByProject_IdAndNameIgnoreCase(UUID projectId, String name);
}