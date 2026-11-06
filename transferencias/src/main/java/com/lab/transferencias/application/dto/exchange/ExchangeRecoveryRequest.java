package com.lab.transferencias.application.dto.exchange;

import java.util.UUID;

/**
 * Solicitud para recuperar la información original de un intercambio externo y sus hechos asociados.
 */
public record ExchangeRecoveryRequest(
    UUID exchangeId,
    UUID remoteActionId,
    UUID effectId,
    UUID participantId,
    String mechanismCode,
    String recoveryReason
) {
}

