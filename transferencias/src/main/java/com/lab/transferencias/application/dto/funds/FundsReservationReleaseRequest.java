package com.lab.transferencias.application.dto.funds;

import java.util.UUID;

/**
 * Solicitud para liberar una reserva que ya no debe ser retenida por la transferencia.
 */
public record FundsReservationReleaseRequest(
    UUID remoteActionId,
    UUID effectId,
    UUID reservationId,
    String authorizationReference,
    String reason
) {
}

