package com.lab.transferencias.domain.valueobject;

import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.enums.DestinationType;
import com.lab.transferencias.domain.exception.DomainInvariantException;

/**
 * Expresa el destino al que se dirige una devolución, separado del destino original aceptado.
 */
public record ReturnDestination(
    DestinationType destinationType,
    ObjectReference accountReference,
    ObjectReference participantReference,
    String bankIdentifierType,
    String bankIdentifierValue,
    ObjectReference resolutionReference,
    Currency currency
) {

  public ReturnDestination {
    if (destinationType == null) {
      throw new DomainInvariantException("destinationType es obligatorio.");
    }
    if (currency == null) {
      throw new DomainInvariantException("currency es obligatorio.");
    }
    if (bankIdentifierType == null && bankIdentifierValue != null) {
      throw new DomainInvariantException("bankIdentifierType y bankIdentifierValue deben aparecer juntos.");
    }
    if (bankIdentifierType != null && bankIdentifierValue == null) {
      throw new DomainInvariantException("bankIdentifierType y bankIdentifierValue deben aparecer juntos.");
    }
    if (bankIdentifierType != null && bankIdentifierType.isBlank()) {
      throw new DomainInvariantException("bankIdentifierType no puede estar vacío.");
    }
    if (bankIdentifierValue != null && bankIdentifierValue.isBlank()) {
      throw new DomainInvariantException("bankIdentifierValue no puede estar vacío.");
    }
    if (destinationType == DestinationType.INTERNAL) {
      if (accountReference == null) {
        throw new DomainInvariantException("accountReference es obligatorio cuando el retorno es interno.");
      }
      if (participantReference != null) {
        throw new DomainInvariantException("participantReference debe ser nulo cuando el retorno es interno.");
      }
    } else if (destinationType == DestinationType.EXTERNAL) {
      if (participantReference == null) {
        throw new DomainInvariantException("participantReference es obligatorio cuando el retorno es externo.");
      }
      if (accountReference != null) {
        throw new DomainInvariantException("accountReference debe ser nulo cuando el retorno es externo.");
      }
    }
  }
}
