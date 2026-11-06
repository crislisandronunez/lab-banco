package com.lab.transferencias.application.dto.funds;

import java.util.UUID;

/**
 * Solicitud para recuperar una actuación monetaria original y su efecto relacionado.
 */
public record FundsRecoveryRequest(
    UUID remoteActionId,
    UUID effectId,
    String sourceAuthority,
    String recoveryReason
) {
}

