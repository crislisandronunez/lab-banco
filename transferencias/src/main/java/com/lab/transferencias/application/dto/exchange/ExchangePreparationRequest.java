package com.lab.transferencias.application.dto.exchange;

import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.valueobject.MonetaryConcept;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Solicitud de preparación de un intercambio externo con la intención y la cobertura pendientes.
 */
public record ExchangePreparationRequest(
    UUID transferId,
    UUID actionId,
    UUID remoteActionId,
    UUID effectId,
    String participantCode,
    String mechanismCode,
    BigDecimal principalAmount,
    Currency currency,
    List<MonetaryConcept> concepts,
    String authorizationReference,
    String externalReference
) {
}

