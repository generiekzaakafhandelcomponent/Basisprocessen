package com.ritense.valtimo.implementation.service;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.ritense.document.domain.impl.JsonSchemaDocument;
import com.ritense.document.domain.impl.JsonSchemaDocumentId;
import com.ritense.document.domain.impl.request.ModifyDocumentRequest;
import com.ritense.document.exception.DocumentNotFoundException;
import com.ritense.document.service.DocumentService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CommonDocumentService {
    private final DocumentService documentService;

    public CommonDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public JsonSchemaDocument findDocumentById(String documentId) {
        return (JsonSchemaDocument) documentService.findBy
            (JsonSchemaDocumentId.existingId(UUID.fromString(documentId)))
            .orElseThrow(() -> {
                throw new DocumentNotFoundException("Document not found");
            });
    }

    public void modifyDocument(JsonSchemaDocument document, JsonNode root) {
        final var documentRequest = new ModifyDocumentRequest(
            document.id().toString(),
            root,
            document.version().toString()
        );
        final var modifyResult = documentService.modifyDocument(documentRequest);
        if (modifyResult.errors().size() > 0) {
            throw new RuntimeException("Document not updated!");
        }
    }

    public String getOrThrowEmptyText(JsonSchemaDocument document, String path) {
        return document.content().getValueBy(JsonPointer.valueOf(path)).orElse(TextNode.valueOf("")).asText();
    }
}
