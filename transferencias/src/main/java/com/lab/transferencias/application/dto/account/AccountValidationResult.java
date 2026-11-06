package com.lab.transferencias.application.dto.account;

import java.util.UUID;
import java.time.Instant;
import java.util.List;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Resultado de la validación de una cuenta frente a una operación financiera.
 */
public record AccountValidationResult(
    UUID accountId,
    boolean valid,
    String status,
    String currencyCode,
    List<String> capabilities,
    List<String> restrictions,
    ObjectReference authorityReference,
    List<ObjectReference> evidenceReferences,
    Instant evaluatedAt
) {

  public AccountValidationResult {
    if (accountId == null || status == null || status.isBlank() || currencyCode == null || currencyCode.isBlank())
      throw new IllegalArgumentException("accountId, status y currencyCode son obligatorios.");
    capabilities = List.copyOf(capabilities);
    restrictions = List.copyOf(restrictions);
    evidenceReferences = List.copyOf(evidenceReferences);
    if (authorityReference == null || evaluatedAt == null)
      throw new IllegalArgumentException("authorityReference y evaluatedAt son obligatorios.");
  }
}
