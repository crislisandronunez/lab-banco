package com.lab.transferencias.domain.enums;

/**
 * Representa el estado general de una transferencia durante su validación, ejecución y cierre.
 */
public enum ProcessingState {
  RECEIVED,
  VALIDATING,
  PREPARING,
  EXECUTING,
  COMPLETED,
  CANCELLED,
  REJECTED,
  REQUIRES_RESOLUTION
}
