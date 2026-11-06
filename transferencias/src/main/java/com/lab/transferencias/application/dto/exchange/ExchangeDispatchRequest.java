package com.lab.transferencias.application.dto.exchange;

import java.util.List;
import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Solicitud para despachar un intercambio ya preparado con su evidencia y referencia de cancelación.
 */
public record ExchangeDispatchRequest(
    UUID exchangeId,
    UUID remoteActionId,
    UUID effectId,
    UUID participantId,
    String mechanismCode,
    List<ObjectReference> evidenceReferences,
    ObjectReference authorizationReference,
    ObjectReference cancellationReference
) {

  public ExchangeDispatchRequest {
    evidenceReferences = List.copyOf(evidenceReferences);
  }
}

