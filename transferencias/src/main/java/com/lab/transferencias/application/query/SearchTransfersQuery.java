package com.lab.transferencias.application.query;

import java.util.UUID;

/** Busca una transferencia por su identidad externa completa. */
public record SearchTransfersQuery(
    String purpose,
    UUID participantId,
    String mechanismCode,
    long mechanismVersion,
    String externalOperationId
) {

  public SearchTransfersQuery {
    if (purpose == null || purpose.isBlank()) throw new IllegalArgumentException("purpose no puede estar vacío.");
    if (participantId == null) throw new IllegalArgumentException("participantId no puede ser nulo.");
    if (mechanismCode == null || mechanismCode.isBlank()) throw new IllegalArgumentException("mechanismCode no puede estar vacío.");
    if (mechanismVersion <= 0) throw new IllegalArgumentException("mechanismVersion debe ser positiva.");
    if (externalOperationId == null || externalOperationId.isBlank()) throw new IllegalArgumentException("externalOperationId no puede estar vacío.");
  }
}
