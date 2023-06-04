package com.ritense.valtimo.implementation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ritense.document.domain.impl.JsonSchemaDocument;
import com.ritense.openzaak.service.ZaakInstanceLinkService;
import com.ritense.openzaak.service.ZaakService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OpenZaakPropertiesService {
    private final ZaakService zaakService;
    private final CommonDocumentService commonDocumentService;
    private final ZaakInstanceLinkService zaakInstanceLinkService;

    public OpenZaakPropertiesService(ZaakService zaakService, CommonDocumentService commonDocumentService, ZaakInstanceLinkService zaakInstanceLinkService) {
        this.zaakService = zaakService;
        this.commonDocumentService = commonDocumentService;
        this.zaakInstanceLinkService = zaakInstanceLinkService;
    }

    public void getOpenZaakId(DelegateExecution execution) {
        var document = commonDocumentService.findDocumentById(execution.getBusinessKey());
        var zaakProperties = zaakService.getZaak(getZaakId(UUID.fromString(execution.getBusinessKey())));
        String zaakId = getOrReturnEmpty(zaakProperties.getIdentificatie());
        modifyOpenZaakId(document, zaakId);
    }

    public void getOpenZaakStatus(DelegateExecution execution) {
        var document = commonDocumentService.findDocumentById(execution.getBusinessKey());
        var zaakProperties = zaakService.getZaak(getZaakId(UUID.fromString(execution.getBusinessKey())));
        String status = getOrReturnEmpty(zaakProperties.getStatusOmschrijving());
        modifyOpenZaakStatus(document, status);
    }

    public void getOpenZaakResultaat(DelegateExecution execution) {
        var document = commonDocumentService.findDocumentById(execution.getBusinessKey());
        var zaakProperties = zaakService.getZaak(getZaakId(UUID.fromString(execution.getBusinessKey())));
        String resultaat = getOrReturnEmpty(zaakProperties.getResulaatOmschrijving());
        modifyOpenZaakResultaat(document, resultaat);
    }

    private void modifyOpenZaakId(JsonSchemaDocument document, String zaaknummer) {
        var root = new ObjectMapper().createObjectNode();
        var openZaak = new ObjectMapper().createObjectNode();
        openZaak.put("zaaknummer", zaaknummer);
        root.set("openZaak", openZaak);
        commonDocumentService.modifyDocument(document, root);
    }

    private void modifyOpenZaakStatus(JsonSchemaDocument document, String status) {
        var root = new ObjectMapper().createObjectNode();
        var openZaak = new ObjectMapper().createObjectNode();
        openZaak.put("status", status);
        root.set("openZaak", openZaak);
        commonDocumentService.modifyDocument(document, root);
    }

    private void modifyOpenZaakResultaat(JsonSchemaDocument document, String resultaat) {
        var root = new ObjectMapper().createObjectNode();
        var openZaak = new ObjectMapper().createObjectNode();
        openZaak.put("resultaat", resultaat);
        root.set("openZaak", openZaak);
        commonDocumentService.modifyDocument(document, root);
    }

    private String getOrReturnEmpty(String property) {
        if (property != null) {
            return property;
        }
        return "";
    }

    private UUID getZaakId(UUID documentId) {
        var zaakInstanceLink = zaakInstanceLinkService.getByDocumentId(documentId);
        return zaakInstanceLink.getZaakInstanceId();
    }
}