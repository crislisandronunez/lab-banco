package com.lab.transferencias.application.command;

import com.lab.transferencias.application.exception.InvalidActionContextException;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Contexto confiable de una actuación de negocio para la transferencia interna.
 * Identifica la petición y conserva la intención, la causa y la evidencia requeridas.
 */
public record ActionContext(
    UUID actionId,
    UUID operationId,
    UUID causationId,
    String purpose,
    String reason,
    Long expectedVersion,
    Instant originalReceivedAt,
    List<ObjectReference> evidenceReferences
) {

  public ActionContext {
    if (actionId == null) {
      throw new InvalidActionContextException("actionId no puede ser nulo.");
    }
    if (operationId == null) {
      throw new InvalidActionContextException("operationId no puede ser nulo.");
    }
    if (purpose == null || purpose.isBlank()) {
      throw new InvalidActionContextException("purpose no puede ser nulo ni vacío.");
    }
    if (evidenceReferences == null) {
      throw new InvalidActionContextException("evidenceReferences no puede ser nulo.");
    }
    evidenceReferences = List.copyOf(evidenceReferences);
  }
}

