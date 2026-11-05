package com.lab.transferencias.domain.valueobject;

import com.lab.transferencias.domain.enums.AuthorizationBasisType;
import com.lab.transferencias.domain.exception.DomainInvariantException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa el fundamento autorizado que respalda una actuación concreta.
 */
public record AuthorizationBasis(
    AuthorizationBasisType basisType,
    ObjectReference sourceReference,
    String contentHash,
    List<ObjectReference> actorReferences,
    List<ObjectReference> evidenceReferences
) {

  public AuthorizationBasis {
    if (basisType == null) {
      throw new DomainInvariantException("basisType es obligatorio.");
    }
    if (sourceReference == null) {
      throw new DomainInvariantException("sourceReference es obligatorio.");
    }
    if (contentHash == null || !contentHash.matches("^[a-fA-F0-9]{64}$")) {
      throw new DomainInvariantException("contentHash debe ser un SHA-256 hexadecimal de 64 caracteres.");
    }
    if (actorReferences == null) {
      throw new DomainInvariantException("actorReferences no puede ser nulo.");
    }
    if (evidenceReferences == null) {
      throw new DomainInvariantException("evidenceReferences no puede ser nulo.");
    }
    actorReferences = new ArrayList<>(actorReferences);
    evidenceReferences = new ArrayList<>(evidenceReferences);
    if (actorReferences.isEmpty()) {
      throw new DomainInvariantException("actorReferences debe contener al menos un actor.");
    }
    if (evidenceReferences.isEmpty()) {
      throw new DomainInvariantException("evidenceReferences debe contener al menos una evidencia.");
    }
    for (ObjectReference actor : actorReferences) {
      if (actor == null) {
        throw new DomainInvariantException("actorReferences no puede contener elementos nulos.");
      }
    }
    for (ObjectReference evidence : evidenceReferences) {
      if (evidence == null) {
        throw new DomainInvariantException("evidenceReferences no puede contener elementos nulos.");
      }
    }
    actorReferences = Collections.unmodifiableList(actorReferences);
    evidenceReferences = Collections.unmodifiableList(evidenceReferences);
  }
}
