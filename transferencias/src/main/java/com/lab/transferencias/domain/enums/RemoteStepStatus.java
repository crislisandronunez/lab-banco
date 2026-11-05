package com.lab.transferencias.domain.enums;

/**
 * Indica el estado de una actuación remota asociada a una devolución o recuperación de resultados.
 */
public enum RemoteStepStatus {
  PENDING,
  CONFIRMED,
  REJECTED,
  UNCERTAIN,
  REQUIRES_REVIEW
}
