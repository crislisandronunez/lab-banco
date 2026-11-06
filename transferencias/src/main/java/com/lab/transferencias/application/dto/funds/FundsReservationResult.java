package com.lab.transferencias.application.dto.funds;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Resultado de una operación de reserva con identidad, firmeza y evidencias recuperables.
 */
public record FundsReservationResult(
    UUID remoteActionId,
    UUID effectId,
    UUID reservationId,
    String outcome,
    String reservationStatus,
    List<ObjectReference> evidenceReferences,
    Instant confirmedAt,
    String message
) {

  public FundsReservationResult {
    evidenceReferences = List.copyOf(evidenceReferences);
  }
}

