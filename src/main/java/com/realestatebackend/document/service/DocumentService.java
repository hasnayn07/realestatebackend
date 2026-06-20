package com.realestatebackend.document.service;

import com.realestatebackend.document.dto.DocumentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface DocumentService {
    DocumentResponse uploadDocument(MultipartFile file, String category, UUID entityId, String entityType);
    List<DocumentResponse> getEntityDocuments(UUID entityId, String entityType);
}