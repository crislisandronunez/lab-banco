package com.lab.transferencias.domain.valueobject;

import com.lab.transferencias.domain.enums.ConfirmedFactType;
import com.lab.transferencias.domain.exception.DomainInvariantException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Expresa un hecho comprobado en la vista del negocio con su fuente y evidencias.
 */
public record ConfirmedFact(
    ConfirmedFactType factType,
    Instant occurredAt,
    ObjectReference sourceReference,
    List<ObjectReference> evidenceReferences
) {

  public ConfirmedFact {
    if (factType == null) {
      throw new DomainInvariantException("factType es obligatorio.");
    }
    if (occurredAt == null) {
      throw new DomainInvariantException("occurredAt es obligatorio.");
    }
    if (sourceReference == null) {
      throw new DomainInvariantException("sourceReference es obligatorio.");
    }
    if (evidenceReferences == null) {
      throw new DomainInvariantException("evidenceReferences no puede ser nulo.");
    }
    evidenceReferences = new ArrayList<>(evidenceReferences);
    if (evidenceReferences.isEmpty())
      throw new DomainInvariantException("Un hecho confirmado requiere evidencia.");
    for (ObjectReference evidence : evidenceReferences) {
      if (evidence == null) {
        throw new DomainInvariantException("evidenceReferences no puede contener elementos nulos.");
      }
    }
    evidenceReferences = Collections.unmodifiableList(evidenceReferences);
  }
}
