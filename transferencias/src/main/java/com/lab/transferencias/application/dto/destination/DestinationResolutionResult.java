package com.lab.transferencias.application.dto.destination;

import com.lab.transferencias.domain.valueobject.ObjectReference;
import java.time.Instant;
import java.util.List;

/**
 * Resultado de resolución de un destino externo o interno para la operación.
 */
public record DestinationResolutionResult(
    boolean resolved,
    String destinationKey,
    String beneficiaryName,
    String status,
    String reason,
    ObjectReference accountReference,
    ObjectReference participantReference,
    ObjectReference beneficiaryReference,
    ObjectReference resolutionReference,
    List<ObjectReference> evidenceReferences,
    Instant resolvedAt
) {

  public DestinationResolutionResult {
    if (status == null || status.isBlank()) throw new IllegalArgumentException("status no puede estar vacío.");
    evidenceReferences = List.copyOf(evidenceReferences);
    if (resolved && (resolutionReference == null || resolvedAt == null))
      throw new IllegalArgumentException("Una resolución positiva requiere referencia y momento.");
  }
}
