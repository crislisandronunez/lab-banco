package com.lab.transferencias.domain.valueobject;

import com.lab.transferencias.domain.exception.DomainInvariantException;

/**
 * Identifica un componente pendiente de resolución y la autoridad responsable de atenderlo.
 */
public record PendingComponent(
    String componentCode,
    String responsibleService,
    String reasonCode,
    ObjectReference reference
) {

  public PendingComponent {
    if (componentCode == null || componentCode.isBlank()) {
      throw new DomainInvariantException("componentCode no puede ser nulo ni vacío.");
    }
    if (componentCode.length() > 80) throw new DomainInvariantException("componentCode no puede exceder 80 caracteres.");
    if (responsibleService == null || responsibleService.isBlank()) {
      throw new DomainInvariantException("responsibleService no puede ser nulo ni vacío.");
    }
    if (responsibleService.length() > 80) throw new DomainInvariantException("responsibleService no puede exceder 80 caracteres.");
    if (reasonCode == null || reasonCode.isBlank()) {
      throw new DomainInvariantException("reasonCode no puede ser nulo ni vacío.");
    }
    if (reasonCode.length() > 80) throw new DomainInvariantException("reasonCode no puede exceder 80 caracteres.");
    if (reference == null) {
      throw new DomainInvariantException("reference es obligatoria para identificar el componente pendiente.");
    }
  }
}
