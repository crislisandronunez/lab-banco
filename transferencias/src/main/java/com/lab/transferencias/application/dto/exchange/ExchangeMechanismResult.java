package com.lab.transferencias.application.dto.exchange;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Resultado de validación de un mecanismo externo con autoridad responsable y evidencias.
 */
public record ExchangeMechanismResult(
    UUID participantId,
    String mechanismCode,
    long mechanismVersion,
    boolean enabled,
    String status,
    List<ObjectReference> evidenceReferences,
    Instant checkedAt,
    String message
) {

  public ExchangeMechanismResult {
    if (mechanismVersion <= 0)
      throw new IllegalArgumentException("mechanismVersion debe ser positiva.");
    evidenceReferences = List.copyOf(evidenceReferences);
  }
}

