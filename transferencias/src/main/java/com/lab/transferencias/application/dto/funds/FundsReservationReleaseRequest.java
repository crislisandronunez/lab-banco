package com.lab.transferencias.application.dto.funds;

import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Solicitud para liberar una reserva que ya no debe ser retenida por la transferencia.
 */
public record FundsReservationReleaseRequest(
    UUID remoteActionId,
    UUID effectId,
    UUID reservationId,
    ObjectReference authorizationReference,
    String reason
) {
}

