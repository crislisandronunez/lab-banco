package com.lab.transferencias.domain.enums;

/**
 * Representa el resultado final de una solicitud de cancelación de una transferencia.
 */
public enum CancellationOutcome {
  CANCELLED,
  PENDING_EXTERNAL_CONFIRMATION,
  ALREADY_CREDITED,
  REQUIRES_RESOLUTION
}
