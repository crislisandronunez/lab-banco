package com.lab.transferencias.application.dto.exchange;

import com.lab.transferencias.application.command.ExternalOperationReference;
import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.enums.DestinationType;
import com.lab.transferencias.domain.valueobject.MonetaryConcept;
import com.lab.transferencias.domain.valueobject.AcceptedDestination;
import com.lab.transferencias.domain.valueobject.ObjectReference;
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
    UUID participantId,
    String mechanismCode,
    AcceptedDestination acceptedDestination,
    BigDecimal principalAmount,
    Currency currency,
    List<MonetaryConcept> concepts,
    ObjectReference authorizationReference,
    ExternalOperationReference externalReference
) {

  public ExchangePreparationRequest {
    if (acceptedDestination == null || acceptedDestination.destinationType() != DestinationType.EXTERNAL)
      throw new IllegalArgumentException("acceptedDestination no puede ser nulo.");
    if (externalReference == null)
      throw new IllegalArgumentException("externalReference no puede ser nula.");
    if (principalAmount == null || principalAmount.signum() <= 0)
      throw new IllegalArgumentException("principalAmount debe ser positivo.");
    if (currency == null || acceptedDestination.currency() != currency)
      throw new IllegalArgumentException("La moneda debe coincidir con el destino aceptado.");
    concepts = List.copyOf(concepts);
  }
}

