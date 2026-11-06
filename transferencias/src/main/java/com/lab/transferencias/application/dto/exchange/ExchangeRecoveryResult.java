package com.lab.transferencias.application.dto.exchange;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Resultado de la recuperación de un intercambio externo con evidencia y estado recuperables.
 */
public record ExchangeRecoveryResult(
    UUID exchangeId,
    UUID remoteActionId,
    UUID effectId,
    String outcome,
    String status,
    List<String> evidenceReferences,
    Instant recoveredAt,
    String message
) {
}

