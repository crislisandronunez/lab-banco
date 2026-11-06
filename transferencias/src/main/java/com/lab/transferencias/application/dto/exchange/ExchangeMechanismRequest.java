package com.lab.transferencias.application.dto.exchange;

import java.util.UUID;

/**
 * Solicitud para comprobar que un mecanismo externo está habilitado para una operación de transferencia.
 */
public record ExchangeMechanismRequest(
    UUID transferId,
    UUID actionId,
    String participantCode,
    String mechanismCode,
    String operationType,
    String version
) {
}

