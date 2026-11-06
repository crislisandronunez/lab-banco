package com.lab.transferencias.application.dto.funds;

import java.util.UUID;

/**
 * Solicitud para reforzar o confirmar una reserva previamente creada para una transferencia.
 */
public record FundsReservationProtectionRequest(
    UUID remoteActionId,
    UUID effectId,
    UUID reservationId,
    String authorizationReference,
    String reservationReference
) {
}

