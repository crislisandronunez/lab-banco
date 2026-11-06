package com.lab.transferencias.application.dto.exchange;

import java.util.List;
import java.util.UUID;

/**
 * Solicitud para despachar un intercambio ya preparado con su evidencia y referencia de cancelación.
 */
public record ExchangeDispatchRequest(
    UUID exchangeId,
    UUID remoteActionId,
    UUID effectId,
    String participantCode,
    String mechanismCode,
    List<String> evidenceReferences,
    String authorizationReference,
    String cancellationReference
) {
}

