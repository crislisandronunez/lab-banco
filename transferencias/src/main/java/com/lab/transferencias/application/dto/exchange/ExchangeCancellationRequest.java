package com.lab.transferencias.application.dto.exchange;

import java.util.List;
import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Solicitud de cancelación de una operación de intercambio ya preparada o despachada.
 */
public record ExchangeCancellationRequest(
    UUID exchangeId,
    UUID remoteActionId,
    UUID effectId,
    UUID participantId,
    String mechanismCode,
    List<ObjectReference> evidenceReferences,
    ObjectReference authorizationReference,
    String reason
) {

  public ExchangeCancellationRequest {
    evidenceReferences = List.copyOf(evidenceReferences);
  }
}

