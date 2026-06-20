package com.realestatebackend.document.service.impl;

import com.realestatebackend.auth.entity.User;
import com.realestatebackend.auth.repository.UserRepository;
import com.realestatebackend.common.exception.NotFoundException;
import com.realestatebackend.document.dto.DocumentResponse;
import com.realestatebackend.document.entity.Document;
import com.realestatebackend.document.repository.DocumentRepository;
import com.realestatebackend.document.service.DocumentService;
import com.realestatebackend.document.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public DocumentResponse uploadDocument(MultipartFile file, String category, UUID entityId, String entityType) {
        String generatedFileName = fileStorageService.storeFile(file);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User uploader = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Authenticated user not found"));

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown_file";

        Document document = Document.builder()
                .fileName(generatedFileName)
                .originalName(StringUtils.cleanPath(originalFilename))
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .documentCategory(category)
                .relatedEntityId(entityId)
                .relatedEntityType(entityType.toUpperCase())
                .uploadedBy(uploader)
                .build();

        document = documentRepository.save(document);
        log.info("Document '{}' uploaded by {}", generatedFileName, uploader.getFullName());

        return toResponse(document);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getEntityDocuments(UUID entityId, String entityType) {
        return documentRepository.findByRelatedEntityIdAndRelatedEntityType(entityId, entityType.toUpperCase())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private DocumentResponse toResponse(Document doc) {
        return new DocumentResponse(
                doc.getId(),
                doc.getFileName(),
                doc.getOriginalName(),
                doc.getFileType(),
                doc.getFileSize(),
                doc.getDocumentCategory(),
                doc.getRelatedEntityId(),
                doc.getRelatedEntityType(),
                doc.getUploadedBy().getFullName(),
                doc.getCreatedAt()
        );
    }
}