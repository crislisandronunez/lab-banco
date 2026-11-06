package com.lab.transferencias.application.dto.exchange;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Resultado de una solicitud de devolución del intercambio externo.
 */
public record ExchangeReturnResult(
    UUID exchangeId,
    UUID remoteActionId,
    UUID effectId,
    String outcome,
    String status,
    List<ObjectReference> evidenceReferences,
    Instant returnedAt,
    String message
) {

  public ExchangeReturnResult {
    evidenceReferences = List.copyOf(evidenceReferences);
  }
}

