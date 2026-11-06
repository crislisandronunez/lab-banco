package com.lab.transferencias.application.dto.exchange;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Resultado de despacho de un intercambio con estados explícitos de envío, aceptación y acreditación.
 */
public record ExchangeDispatchResult(
    UUID exchangeId,
    UUID remoteActionId,
    UUID effectId,
    String outcome,
    String status,
    List<ObjectReference> evidenceReferences,
    Instant dispatchedAt,
    Instant acceptedAt,
    String message
) {

  public ExchangeDispatchResult {
    evidenceReferences = List.copyOf(evidenceReferences);
  }
}

