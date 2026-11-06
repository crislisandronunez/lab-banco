package com.lab.transferencias.application.dto.exchange;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Resultado de una solicitud de cancelación de un intercambio externo.
 */
public record ExchangeCancellationResult(
    UUID exchangeId,
    UUID remoteActionId,
    UUID effectId,
    String outcome,
    String status,
    List<ObjectReference> evidenceReferences,
    Instant requestedAt,
    String message
) {

  public ExchangeCancellationResult {
    evidenceReferences = List.copyOf(evidenceReferences);
  }
}

