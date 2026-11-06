package com.lab.transferencias.application.dto.exchange;

import java.time.Instant;
import java.util.UUID;

/**
 * Resultado de una preparación o ejecución de intercambio externo.
 */
public record ExchangeExecutionResult(
    UUID exchangeId,
    String status,
    String message,
    Instant createdAt,
    String externalReference
) {
}
