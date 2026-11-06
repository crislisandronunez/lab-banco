package com.lab.transferencias.domain.valueobject;

import com.lab.transferencias.domain.exception.DomainInvariantException;

/**
 * Conserva la identidad de un objeto administrado por este u otro servicio.
 */
public record ObjectReference(
    String objectType,
    Object objectId,
    String sourceService,
    Long version
) {

  public ObjectReference {
    if (objectType == null || objectType.isBlank()) {
      throw new DomainInvariantException("objectType no puede ser nulo ni vacío.");
    }
    if (objectType.length() > 80) throw new DomainInvariantException("objectType no puede exceder 80 caracteres.");
    if (objectId == null) {
      throw new DomainInvariantException("objectId no puede ser nulo.");
    }
    if (!(objectId instanceof String) && !(objectId instanceof java.util.UUID))
      throw new DomainInvariantException("objectId debe conservarse como String o UUID tipado.");
    if (objectId instanceof String id && id.isBlank())
      throw new DomainInvariantException("objectId no puede estar vacío.");
    if (sourceService == null || sourceService.isBlank()) {
      throw new DomainInvariantException("sourceService no puede ser nulo ni vacío.");
    }
    if (sourceService.length() > 80) throw new DomainInvariantException("sourceService no puede exceder 80 caracteres.");
    if (version != null && version <= 0) {
      throw new DomainInvariantException("version debe ser positiva cuando se proporciona.");
    }
  }
}
