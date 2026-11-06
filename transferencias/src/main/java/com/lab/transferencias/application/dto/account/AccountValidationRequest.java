package com.lab.transferencias.application.dto.account;

import java.util.UUID;

/**
 * Solicitud de validación de una cuenta para un acto de transferencia.
 */
public record AccountValidationRequest(
    UUID accountId,
    UUID actorId,
    String operationType,
    String currencyCode
) {
}
