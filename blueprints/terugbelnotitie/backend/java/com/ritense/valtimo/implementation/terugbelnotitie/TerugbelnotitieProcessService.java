package com.ritense.valtimo.implementation.terugbelnotitie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ritense.valtimo.contract.authentication.CurrentUserService;
import com.ritense.valtimo.contract.authentication.ManageableUser;
import com.ritense.valtimo.implementation.service.CommonDocumentService;
import com.valtimo.keycloak.service.KeycloakUserManagementService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.util.Optional;


@RequiredArgsConstructor
@Component
public class TerugbelnotitieProcessService {

    private final CommonDocumentService commonDocumentService;
    private final CurrentUserService currentUserService;
    private final KeycloakUserManagementService keycloakUserManagementService;

    public void setInterneStatus(DelegateExecution execution, String status) {
        var document = commonDocumentService.findDocumentById(execution.getBusinessKey());

        ObjectNode root = new ObjectMapper().createObjectNode();
        root.put("interneStatus", status);
        commonDocumentService.modifyDocument(document, root);
    }

    public void addCurrentUserToDocument(DelegateExecution execution) throws IllegalAccessException {
        var document = commonDocumentService.findDocumentById(execution.getBusinessKey());

        ObjectNode root = new ObjectMapper().createObjectNode();
        ObjectNode caseMetadata = new ObjectMapper().createObjectNode();
        Optional<ManageableUser> currentUser = keycloakUserManagementService
            .findByEmail(currentUserService.getCurrentUser().getEmail());
        currentUser.ifPresent(manageableUser ->
            caseMetadata.put(
                "aangemaaktDoor",
                manageableUser.getFirstName() + " " + manageableUser.getLastName())
        );

        root.set("caseMetadata", caseMetadata);
        commonDocumentService.modifyDocument(document, root);
    }
}