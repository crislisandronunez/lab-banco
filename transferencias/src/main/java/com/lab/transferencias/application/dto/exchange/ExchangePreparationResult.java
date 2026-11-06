package com.lab.transferencias.application.dto.exchange;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Resultado de preparar un intercambio externo sin afirmar que ya fue enviado o aceptado.
 */
public record ExchangePreparationResult(
    UUID exchangeId,
    UUID remoteActionId,
    UUID effectId,
    String outcome,
    String status,
    List<String> evidenceReferences,
    Instant preparedAt,
    String message
) {
}

