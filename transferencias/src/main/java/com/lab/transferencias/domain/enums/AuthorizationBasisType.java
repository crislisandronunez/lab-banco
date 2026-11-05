package com.lab.transferencias.domain.enums;

/**
 * Define el fundamento que autoriza una actuación de transferencia o cancelación.
 */
public enum AuthorizationBasisType {
  DIRECT_INSTRUCTION,
  MANDATE,
  OCCURRENCE,
  CONTRACTUAL_OBLIGATION,
  AUTHORIZED_RESOLUTION
}
