package com.lab.transferencias.application.querys;

/**
 * Consulta para buscar transferencias a partir de una identidad externa o referencial.
 * Se usa para localizar una instrucción por criterios no ambiguos del mecanismo o del participante.
 */
public record SearchTransfersQuery(
    String externalOperationId,
    String sourceParticipantId,
    String destinationParticipantId,
    String status,
    Integer page,
    Integer size
) {

  public SearchTransfersQuery {
    if (externalOperationId == null || externalOperationId.isBlank()) {
      throw new IllegalArgumentException("externalOperationId no puede ser nulo ni vacío.");
    }
    if (sourceParticipantId == null || sourceParticipantId.isBlank()) {
      throw new IllegalArgumentException("sourceParticipantId no puede ser nulo ni vacío.");
    }
    if (destinationParticipantId == null || destinationParticipantId.isBlank()) {
      throw new IllegalArgumentException("destinationParticipantId no puede ser nulo ni vacío.");
    }
    if (page != null && page < 0) {
      throw new IllegalArgumentException("page no puede ser negativo.");
    }
    if (size != null && size <= 0) {
      throw new IllegalArgumentException("size debe ser positivo cuando se informa.");
    }
  }
}

