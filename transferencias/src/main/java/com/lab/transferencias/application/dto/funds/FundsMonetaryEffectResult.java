package com.lab.transferencias.application.dto.funds;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Resultado de una operación de crédito o de devolución con estado explícito y evidencias verificables.
 */
public record FundsMonetaryEffectResult(
    UUID remoteActionId,
    UUID effectId,
    String outcome,
    UUID debitReference,
    UUID creditReference,
    List<ObjectReference> evidenceReferences,
    Instant confirmedAt,
    String message
) {

  public FundsMonetaryEffectResult {
    evidenceReferences = List.copyOf(evidenceReferences);
  }
}

