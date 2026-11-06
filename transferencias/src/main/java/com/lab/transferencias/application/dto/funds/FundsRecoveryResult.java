package com.lab.transferencias.application.dto.funds;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Resultado de recuperación de una actuación monetaria original con su efecto y evidencias.
 */
public record FundsRecoveryResult(
    UUID remoteActionId,
    UUID effectId,
    String outcome,
    String status,
    List<ObjectReference> evidenceReferences,
    Instant recoveredAt,
    String message
) {

  public FundsRecoveryResult {
    evidenceReferences = List.copyOf(evidenceReferences);
  }
}

