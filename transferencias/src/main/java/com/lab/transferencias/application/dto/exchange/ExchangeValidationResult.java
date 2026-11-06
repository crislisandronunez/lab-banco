package com.lab.transferencias.application.dto.exchange;

import java.time.Instant;
import java.util.UUID;

/**
 * Resultado de validación del mecanismo o intercambio externo requerido por la operación.
 */
public record ExchangeValidationResult(
    UUID exchangeId,
    boolean valid,
    String status,
    String participantCode,
    Instant validFrom,
    Instant validTo,
    String message
) {
}
