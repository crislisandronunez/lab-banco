package com.lab.transferencias.application.command;

import com.lab.transferencias.application.dto.identity.TransferOriginIdentity;
import java.util.UUID;

/**
 * Contexto verificado de invocación para la operación de transferencia.
 * Conserva la identidad verificada del actor y el servicio llamador.
 */
public record RequestContext(
    UUID actorId,
    String actorType,
    String callerService,
    String scope,
    String idempotencyKey,
    String correlationId,
    TransferOriginIdentity originIdentity
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
    if (idempotencyKey != null && idempotencyKey.isBlank()) {
      throw new IllegalArgumentException("idempotencyKey no puede estar vacío.");
    }
    if (correlationId != null && correlationId.isBlank()) {
      throw new IllegalArgumentException("correlationId no puede estar vacío.");
    }
  }
}

