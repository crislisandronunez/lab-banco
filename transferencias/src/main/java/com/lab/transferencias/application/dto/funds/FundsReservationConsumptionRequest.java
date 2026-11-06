package com.lab.transferencias.application.dto.funds;

import java.util.List;
import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Solicitud para consumir una reserva con la evidencia del efecto económico confirmado.
 */
public record FundsReservationConsumptionRequest(
    UUID remoteActionId,
    UUID effectId,
    UUID reservationId,
    List<ObjectReference> evidenceReferences,
    ObjectReference authorizationReference
) {

  public FundsReservationConsumptionRequest {
    evidenceReferences = List.copyOf(evidenceReferences);
  }
}

