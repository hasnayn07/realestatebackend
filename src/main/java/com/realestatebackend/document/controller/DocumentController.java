package com.realestatebackend.document.controller;

import com.realestatebackend.document.dto.DocumentResponse;
import com.realestatebackend.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'AGENT')")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("category") String category,
            @RequestParam("entityId") UUID entityId,
            @RequestParam("entityType") String entityType) {

        DocumentResponse response = documentService.uploadDocument(file, category, entityId, entityType);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<List<DocumentResponse>> getEntityDocuments(
            @PathVariable String entityType,
            @PathVariable UUID entityId) {
        return ResponseEntity.ok(documentService.getEntityDocuments(entityId, entityType));
    }
}