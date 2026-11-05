package com.lab.transferencias.domain.enums;

/**
 * Representa el resultado verificado de la ejecución monetaria solicitada a Fondos.
 */
public enum MonetaryExecutionStatus {
  NOT_STARTED,
  PENDING,
  CONFIRMED,
  REJECTED,
  UNCERTAIN
}
