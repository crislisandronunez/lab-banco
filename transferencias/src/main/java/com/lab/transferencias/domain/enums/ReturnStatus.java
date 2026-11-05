package com.lab.transferencias.domain.enums;

/**
 * Indica el estado de una devolución registrada para una transferencia ya acreditada o en revisión.
 */
public enum ReturnStatus {
  PENDING,
  CONFIRMED,
  REJECTED,
  UNCERTAIN,
  REQUIRES_REVIEW
}
