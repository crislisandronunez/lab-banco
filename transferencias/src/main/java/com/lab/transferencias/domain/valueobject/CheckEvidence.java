package com.lab.transferencias.domain.valueobject;

import com.lab.transferencias.domain.exception.DomainInvariantException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa el resultado y la evidencia de una comprobación realizada por una autoridad.
 */
public record CheckEvidence(
    String checkCode,
    String decisionCode,
    ObjectReference sourceReference,
    Instant checkedAt,
    List<ObjectReference> evidenceReferences
) {

  public CheckEvidence {
    if (checkCode == null || checkCode.isBlank()) {
      throw new DomainInvariantException("checkCode no puede ser nulo ni vacío.");
    }
    if (checkCode.length() > 80) throw new DomainInvariantException("checkCode no puede exceder 80 caracteres.");
    if (decisionCode == null || decisionCode.isBlank()) {
      throw new DomainInvariantException("decisionCode no puede ser nulo ni vacío.");
    }
    if (decisionCode.length() > 80) throw new DomainInvariantException("decisionCode no puede exceder 80 caracteres.");
    if (sourceReference == null) {
      throw new DomainInvariantException("sourceReference es obligatorio.");
    }
    if (checkedAt == null) {
      throw new DomainInvariantException("checkedAt es obligatorio.");
    }
    if (evidenceReferences == null) {
      throw new DomainInvariantException("evidenceReferences no puede ser nulo.");
    }
    evidenceReferences = new ArrayList<>(evidenceReferences);
    for (ObjectReference evidence : evidenceReferences) {
      if (evidence == null) {
        throw new DomainInvariantException("evidenceReferences no puede contener elementos nulos.");
      }
    }
    evidenceReferences = Collections.unmodifiableList(evidenceReferences);
  }
}
