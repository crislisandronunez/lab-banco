package com.lab.transferencias.application.dto.exchange;

import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.valueobject.MonetaryConcept;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Solicitud para una operación de intercambio externo con identidad remota y cobertura asociada.
 */
public record ExchangeExecutionRequest(
    UUID exchangeId,
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
