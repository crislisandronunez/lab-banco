package com.lab.transferencias.application.dto.exchange;

import java.util.UUID;

/**
 * Solicitud para comprobar que un mecanismo externo está habilitado para una operación de transferencia.
 */
public record ExchangeMechanismRequest(
    UUID transferId,
    UUID actionId,
    UUID participantId,
    String mechanismCode,
    String operationType,
    long mechanismVersion
) {

  public ExchangeMechanismRequest {
    if (mechanismVersion <= 0)
      throw new IllegalArgumentException("mechanismVersion debe ser positiva.");
  }
}

