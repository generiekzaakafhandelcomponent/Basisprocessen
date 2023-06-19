package com.ritense.valtimo.implementation.terugbelnotitie.delegates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ritense.valtimo.implementation.service.CommonDocumentService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class TerugbelnotitieZetNotitiesArrayDelegate implements JavaDelegate {
    private final CommonDocumentService commonDocumentService;

    public TerugbelnotitieZetNotitiesArrayDelegate(CommonDocumentService commonDocumentService) {
        this.commonDocumentService = commonDocumentService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        setNotitiesArray(execution);
    }

    private void setNotitiesArray(DelegateExecution execution) {
        var document = commonDocumentService.findDocumentById(execution.getBusinessKey());
        ObjectNode root = new ObjectMapper().createObjectNode();

        root.putArray("notities");

        commonDocumentService.modifyDocument(document, root);
    }
}
