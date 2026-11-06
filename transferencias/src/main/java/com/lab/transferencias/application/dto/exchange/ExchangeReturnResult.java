package com.lab.transferencias.application.dto.exchange;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Resultado de una solicitud de devolución del intercambio externo.
 */
public record ExchangeReturnResult(
    UUID exchangeId,
    UUID remoteActionId,
    UUID effectId,
    String outcome,
    String status,
    List<String> evidenceReferences,
    Instant returnedAt,
    String message
) {
}

