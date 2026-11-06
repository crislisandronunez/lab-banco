package com.lab.transferencias.application.dto.funds;

import com.lab.transferencias.application.command.ExternalOperationReference;
import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.valueobject.MonetaryConcept;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Solicitud de acreditación de un movimiento de entrada con su identidad y evidencia original.
 */
public record FundsIncomingCreditRequest(
    UUID remoteActionId,
    UUID effectId,
    UUID destinationAccountId,
    BigDecimal principalAmount,
    Currency currency,
    List<MonetaryConcept> concepts,
    ObjectReference authorizationReference,
    ExternalOperationReference externalReference
) {

  public FundsIncomingCreditRequest {
    if (remoteActionId == null || effectId == null || destinationAccountId == null
        || externalReference == null)
      throw new IllegalArgumentException("remoteActionId, effectId y destinationAccountId son obligatorios.");
    if (principalAmount == null || principalAmount.signum() <= 0 || currency == null)
      throw new IllegalArgumentException("principalAmount positivo y currency son obligatorios.");
    concepts = List.copyOf(concepts);
  }
}

