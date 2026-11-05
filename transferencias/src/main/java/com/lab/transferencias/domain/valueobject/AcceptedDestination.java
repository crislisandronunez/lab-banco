package com.lab.transferencias.domain.valueobject;

import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.enums.DestinationType;
import com.lab.transferencias.domain.exception.DomainInvariantException;
import java.time.Instant;

/**
 * Conserva exactamente el destino presentado y aceptado para una transferencia.
 */
public record AcceptedDestination(
    DestinationType destinationType,
    ObjectReference accountReference,
    ObjectReference participantReference,
    String bankIdentifierType,
    String bankIdentifierValue,
    ObjectReference resolutionReference,
    ObjectReference beneficiaryReference,
    String beneficiaryDisplayName,
    Currency currency,
    String acceptedContentHash,
    Instant acceptedAt
) {

  public AcceptedDestination {
    if (destinationType == null) {
      throw new DomainInvariantException("destinationType es obligatorio.");
    }
    if (currency == null) {
      throw new DomainInvariantException("currency es obligatorio.");
    }
    if (acceptedContentHash == null || !acceptedContentHash.matches("^[a-fA-F0-9]{64}$")) {
      throw new DomainInvariantException("acceptedContentHash debe ser un SHA-256 hexadecimal de 64 caracteres.");
    }
    if (acceptedAt == null) {
      throw new DomainInvariantException("acceptedAt es obligatorio.");
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
    if (beneficiaryDisplayName != null && beneficiaryDisplayName.length() > 250) {
      throw new DomainInvariantException("beneficiaryDisplayName excede los 250 caracteres permitidos.");
    }

    if (destinationType == DestinationType.INTERNAL) {
      if (accountReference == null) {
        throw new DomainInvariantException("accountReference es obligatorio para un destino interno.");
      }
      if (participantReference != null) {
        throw new DomainInvariantException("participantReference debe ser nulo para un destino interno.");
      }
    } else if (destinationType == DestinationType.EXTERNAL) {
      if (participantReference == null) {
        throw new DomainInvariantException("participantReference es obligatorio para un destino externo.");
      }
      if (accountReference != null) {
        throw new DomainInvariantException("accountReference debe ser nulo para un destino externo.");
      }
    }
  }
}
