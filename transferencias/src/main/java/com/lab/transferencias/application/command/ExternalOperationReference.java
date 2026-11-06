package com.lab.transferencias.application.command;

import java.util.UUID;

/** Identidad completa de una operación recibida desde un participante externo. */
public record ExternalOperationReference(
    UUID participantId,
    String mechanismCode,
    long mechanismVersion,
    String externalOperationId
) {

  public ExternalOperationReference {

    if (participantId == null) throw new IllegalArgumentException("participantId no puede ser nulo.");

    if (mechanismCode == null || mechanismCode.isBlank())
      throw new IllegalArgumentException("mechanismCode no puede estar vacío.");

    if (mechanismVersion <= 0) throw new IllegalArgumentException("mechanismVersion debe ser positiva.");

    if (externalOperationId == null || externalOperationId.isBlank())
      throw new IllegalArgumentException("externalOperationId no puede estar vacío.");
  }
}
