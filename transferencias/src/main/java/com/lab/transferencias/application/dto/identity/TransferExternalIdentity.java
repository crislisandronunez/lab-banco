package com.lab.transferencias.application.dto.identity;

import java.util.UUID;

/** Identidad empresarial compuesta de una operación externa. */
public record TransferExternalIdentity(
    UUID participantId,
    String mechanismCode,
    String externalOperationId
) {

  public TransferExternalIdentity {
    if (participantId == null) throw new IllegalArgumentException("participantId no puede ser nulo.");
    if (mechanismCode == null || mechanismCode.isBlank())
      throw new IllegalArgumentException("mechanismCode no puede estar vacío.");
    if (externalOperationId == null || externalOperationId.isBlank())
      throw new IllegalArgumentException("externalOperationId no puede estar vacío.");
  }
}
