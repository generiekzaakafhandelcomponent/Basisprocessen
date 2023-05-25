package com.ritense.valtimo.implementation.terugbelnotitie;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ritense.valtimo.contract.authentication.CurrentUserService;
import com.ritense.valtimo.contract.authentication.ManageableUser;
import com.ritense.valtimo.implementation.service.CommonDocumentService;
import com.valtimo.keycloak.service.KeycloakUserManagementService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ToelichtingMedewerkerService {

    private final CommonDocumentService commonDocumentService;
    private final CurrentUserService currentUserService;
    private final KeycloakUserManagementService keycloakUserManagementService;

    public void addTimeStampAndCurrentUserToDocument(DelegateExecution execution) throws IllegalAccessException {
        var document = commonDocumentService.findDocumentById(execution.getBusinessKey());
        ArrayNode toelichtingMedewerker = (ArrayNode) document
            .content()
            .getValueBy(JsonPointer.valueOf("/afhandeling/toelichtingMedewerker"))
            .orElseThrow();

        ArrayNode newToelichtingMedewerker = new ObjectMapper().createArrayNode();
        for (JsonNode toelichting : toelichtingMedewerker) {
            ObjectNode newToelichting = new ObjectMapper().createObjectNode();
            if (toelichting.get("timestamp") == null && toelichting.get("gebruiker") == null) {
                Optional<ManageableUser> currentUser = keycloakUserManagementService
                    .findByEmail(currentUserService.getCurrentUser().getEmail());
                currentUser.ifPresent(manageableUser ->
                    newToelichting.put(
                        "gebruiker",
                        manageableUser.getFirstName() + " " + manageableUser.getLastName()
                    )
                );

                String timeStamp = timeStamp();
                newToelichting.put("timestamp", timeStamp);
                newToelichting.set("toelichting", toelichting.get("toelichting"));
                newToelichtingMedewerker.add(newToelichting);
            }
        }

        for (JsonNode toelichting : toelichtingMedewerker) {
            if (toelichting.get("timestamp") != null && toelichting.get("gebruiker") != null) {
                newToelichtingMedewerker.add(toelichting);
            }
        }

        ObjectNode root = getRootNode(newToelichtingMedewerker);
        commonDocumentService.modifyDocument(document, root);
    }

    private ObjectNode getRootNode(ArrayNode newToelichtingMedewerker) {
        ObjectNode root = new ObjectMapper().createObjectNode();
        ObjectNode afhandeling = new ObjectMapper().createObjectNode();
        afhandeling.set("toelichtingMedewerker", newToelichtingMedewerker);
        root.set("afhandeling", afhandeling);
        return root;
    }

    private String timeStamp() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }
}