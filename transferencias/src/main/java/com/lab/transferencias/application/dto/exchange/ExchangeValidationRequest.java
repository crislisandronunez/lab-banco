package com.lab.transferencias.application.dto.exchange;

import java.util.UUID;

/**
 * Solicitud de validación de un mecanismo externo o intercambio de compensación.
 */
public record ExchangeValidationRequest(
    UUID transferId,
    UUID actionId,
    String mechanismCode,
    String participantCode,
    String operationType
) {
}
