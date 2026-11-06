package com.lab.transferencias.application.dto.funds;

import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Solicitud para reforzar o confirmar una reserva previamente creada para una transferencia.
 */
public record FundsReservationProtectionRequest(
    UUID remoteActionId,
    UUID effectId,
    UUID reservationId,
    ObjectReference authorizationReference,
    ObjectReference reservationReference
) {
}

