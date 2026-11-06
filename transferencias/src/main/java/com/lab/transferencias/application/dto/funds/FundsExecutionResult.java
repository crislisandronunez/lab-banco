package com.lab.transferencias.application.dto.funds;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Resultado de un conjunto de movimientos internos con identidad original, evidencia y resultado verificable.
 */
public record FundsExecutionResult(
    UUID remoteActionId,
    UUID effectId,
    String outcome,
    UUID debitReference,
    UUID creditReference,
    List<ObjectReference> chargeEffectReferences,
    List<ObjectReference> evidenceReferences,
    Instant confirmedAt,
    String message
) {

  public FundsExecutionResult {
    chargeEffectReferences = List.copyOf(chargeEffectReferences);
    evidenceReferences = List.copyOf(evidenceReferences);
  }
}
