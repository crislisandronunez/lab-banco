package com.lab.transferencias.application.dto.representation;

import java.time.Instant;
import java.util.List;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Resultado de la validación de representación en la operación solicitada.
 */
public record RepresentationCheckResult(
    boolean authorized,
    String authority,
    String scope,
    Instant evaluatedAt,
    Instant validUntil,
    String contentHash,
    ObjectReference decisionReference,
    List<ObjectReference> evidenceReferences,
    String justification
) {

  public RepresentationCheckResult {
    evidenceReferences = List.copyOf(evidenceReferences);
    if (authority == null || authority.isBlank() || scope == null || scope.isBlank() || evaluatedAt == null)
      throw new IllegalArgumentException("authority, scope y evaluatedAt son obligatorios.");
    if (decisionReference == null || evidenceReferences.isEmpty())
      throw new IllegalArgumentException("La decisión requiere referencia y evidencia.");
  }
}
