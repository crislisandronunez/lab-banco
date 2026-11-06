package com.lab.transferencias.application.dto.exchange;

import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.valueobject.MonetaryConcept;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Solicitud para tramitar una devolución del intercambio externo asociado a la transferencia.
 */
public record ExchangeReturnRequest(
    UUID exchangeId,
    UUID remoteActionId,
    UUID effectId,
    UUID participantId,
    String mechanismCode,
    BigDecimal principalAmount,
    Currency currency,
    List<MonetaryConcept> concepts,
    ObjectReference authorizationReference,
    String returnReason
) {

  public ExchangeReturnRequest {
    if (exchangeId == null || remoteActionId == null || effectId == null)
      throw new IllegalArgumentException("exchangeId, remoteActionId y effectId son obligatorios.");
    if (principalAmount == null || principalAmount.signum() <= 0 || currency == null)
      throw new IllegalArgumentException("principalAmount positivo y currency son obligatorios.");
    concepts = List.copyOf(concepts);
  }
}

