package com.lab.transferencias.application.command;

import java.util.Objects;
import java.util.UUID;

/**
 * Contexto verificado de invocación para la operación de transferencia.
 * Conserva la identidad del actor, el servicio llamador y la clave de idempotencia de la solicitud.
 */
public record RequestContext(
    UUID actorId,
    String actorType,
    String callerService,
    String scope,
    String idempotencyKey,
    String correlationId
) {

  public RequestContext {
    if (actorId == null) {
      throw new IllegalArgumentException("actorId no puede ser nulo.");
    }
    if (actorType == null || actorType.isBlank()) {
      throw new IllegalArgumentException("actorType no puede ser nulo ni vacío.");
    }
    if (callerService == null || callerService.isBlank()) {
      throw new IllegalArgumentException("callerService no puede ser nulo ni vacío.");
    }
    if (scope == null || scope.isBlank()) {
      throw new IllegalArgumentException("scope no puede ser nulo ni vacío.");
    }
    if (idempotencyKey == null || idempotencyKey.isBlank()) {
      throw new IllegalArgumentException("idempotencyKey no puede ser nulo ni vacío.");
    }
    Objects.requireNonNull(correlationId, "correlationId no puede ser nulo.");
  }
}

