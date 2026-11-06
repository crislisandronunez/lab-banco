package com.lab.transferencias.application.dto.exchange;

import java.util.List;
import java.util.UUID;

/**
 * Solicitud de cancelación de una operación de intercambio ya preparada o despachada.
 */
public record ExchangeCancellationRequest(
    UUID exchangeId,
    UUID remoteActionId,
    UUID effectId,
    String participantCode,
    String mechanismCode,
    List<String> evidenceReferences,
    String authorizationReference,
    String reason
) {
}

