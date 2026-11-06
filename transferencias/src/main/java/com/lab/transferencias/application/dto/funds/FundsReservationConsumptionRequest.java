package com.lab.transferencias.application.dto.funds;

import java.util.List;
import java.util.UUID;

/**
 * Solicitud para consumir una reserva con la evidencia del efecto económico confirmado.
 */
public record FundsReservationConsumptionRequest(
    UUID remoteActionId,
    UUID effectId,
    UUID reservationId,
    List<String> evidenceReferences,
    String authorizationReference
) {
}

