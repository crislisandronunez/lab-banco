package com.lab.transferencias.application.dto.account;

import java.util.UUID;

/**
 * Resultado de la validación de una cuenta frente a una operación financiera.
 */
public record AccountValidationResult(
    UUID accountId,
    boolean valid,
    String status,
    String message,
    String currencyCode
) {
}
