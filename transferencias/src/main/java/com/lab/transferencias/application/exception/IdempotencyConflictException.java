package com.lab.transferencias.application.exception;

import java.util.UUID;

/**
 * Indica que la misma identidad de operación fue recibida con contenido o intención incompatibles.
 * Protege la deduplicación y evita efectos duplicados o contradictorios.
 */
public class IdempotencyConflictException extends ApplicationException {

  public IdempotencyConflictException(UUID actionId, String message) {
    super("IDEMPOTENCY_CONFLICT", message + " (actionId=" + actionId + ")");
  }
}

