package com.ritense.valtimo.implementation.generiekezaak;

import com.ritense.klant.domain.Klant;
import com.ritense.klant.service.KlantService;
import java.util.UUID;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

@Component
public class ProcessKlantService {
    private final KlantService klantService;

    public ProcessKlantService(KlantService klantService) {
        this.klantService = klantService;
    }

    public Klant getKlant(DelegateExecution execution) {
        //assuming business key is document id
        return klantService.getKlantForDocument(UUID.fromString(execution.getBusinessKey()));
    }
}
