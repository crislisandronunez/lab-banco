package com.lab.transferencias.application.dto.controls;

import java.time.Instant;
import java.util.List;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Resultado de un control de negocio aplicado a la operación.
 */
public record BusinessControlResult(
    boolean approved,
    String controlCode,
    String decision,
    String reason,
    Instant evaluatedAt,
    ObjectReference authorityReference,
    List<ObjectReference> evidenceReferences
) {

  public BusinessControlResult {
    if (decision == null || decision.isBlank() || evaluatedAt == null || authorityReference == null)
      throw new IllegalArgumentException("decision, evaluatedAt y authorityReference son obligatorios.");
    evidenceReferences = List.copyOf(evidenceReferences);
    if (evidenceReferences.isEmpty()) throw new IllegalArgumentException("La decisión requiere evidencia.");
  }
}
