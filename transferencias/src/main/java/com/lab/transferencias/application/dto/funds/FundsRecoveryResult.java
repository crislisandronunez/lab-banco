package com.lab.transferencias.application.dto.funds;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Resultado de recuperación de una actuación monetaria original con su efecto y evidencias.
 */
public record FundsRecoveryResult(
    UUID remoteActionId,
    UUID effectId,
    String outcome,
    String status,
    List<String> evidenceReferences,
    Instant recoveredAt,
    String message
) {
}

