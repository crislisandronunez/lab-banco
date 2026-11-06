package com.lab.transferencias.application.dto.funds;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Resultado de un conjunto de movimientos internos con identidad original, evidencia y resultado verificable.
 */
public record FundsExecutionResult(
    UUID remoteActionId,
    UUID effectId,
    String outcome,
    UUID debitReference,
    UUID creditReference,
    List<String> chargeEffectReferences,
    List<String> evidenceReferences,
    Instant confirmedAt,
    String message
) {
}
