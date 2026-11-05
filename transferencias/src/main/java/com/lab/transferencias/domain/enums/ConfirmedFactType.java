package com.lab.transferencias.domain.enums;

/**
 * Define los hechos comprobados que se registran como historial de una transferencia.
 */
public enum ConfirmedFactType {
  RECEIVED,
  ACCEPTED_FOR_EXECUTION,
  DISPATCHED,
  IRREVOCABLY_ACCEPTED,
  INSTRUMENT_EXECUTED,
  BENEFICIARY_CREDITED,
  CREDITOR_APPLIED,
  RETURNED,
  EXTERNALLY_SETTLED
}
