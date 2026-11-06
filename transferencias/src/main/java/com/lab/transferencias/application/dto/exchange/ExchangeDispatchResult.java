package com.lab.transferencias.application.dto.exchange;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Resultado de despacho de un intercambio con estados explícitos de envío, aceptación y acreditación.
 */
public record ExchangeDispatchResult(
    UUID exchangeId,
    UUID remoteActionId,
    UUID effectId,
    String outcome,
    String status,
    List<String> evidenceReferences,
    Instant dispatchedAt,
    Instant acceptedAt,
    String message
) {
}

