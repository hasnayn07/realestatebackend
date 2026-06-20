package com.realestatebackend.document.repository;

import com.realestatebackend.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByRelatedEntityIdAndRelatedEntityType(UUID relatedEntityId, String relatedEntityType);
}