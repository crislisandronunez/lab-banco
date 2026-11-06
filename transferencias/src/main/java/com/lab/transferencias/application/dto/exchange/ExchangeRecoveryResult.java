package com.lab.transferencias.application.dto.exchange;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Resultado de la recuperación de un intercambio externo con evidencia y estado recuperables.
 */
public record ExchangeRecoveryResult(
    UUID exchangeId,
    UUID remoteActionId,
    UUID effectId,
    String outcome,
    String status,
    List<ObjectReference> evidenceReferences,
    Instant recoveredAt,
    String message
) {

  public ExchangeRecoveryResult {
    evidenceReferences = List.copyOf(evidenceReferences);
  }
}

