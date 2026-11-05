package com.lab.transferencias.application.command;

import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.valueobject.AcceptedDestination;
import com.lab.transferencias.domain.valueobject.AuthorizationBasis;
import com.lab.transferencias.domain.valueobject.MonetaryConcept;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Comando para aceptar y procesar una transferencia externa entrante.
 * Describe la instrucción recibida, su origen externo y la validación necesaria antes del crédito.
 */
public record ReceiveIncomingTransferCommand(
    ActionContext context,
    ObjectReference sourceParticipant,
    ObjectReference externalReference,
    UUID destinationAccountId,
    AcceptedDestination acceptedDestination,
    BigDecimal principalAmount,
    Currency currency,
    String purposeCode,
    AuthorizationBasis authorizationBasis,
    List<MonetaryConcept> concepts,
    String externalOperationId,
    String messageChannel
) {

  public ReceiveIncomingTransferCommand {
    if (context == null) {
      throw new IllegalArgumentException("context no puede ser nulo.");
    }
    if (sourceParticipant == null) {
      throw new IllegalArgumentException("sourceParticipant no puede ser nulo.");
    }
    if (externalReference == null) {
      throw new IllegalArgumentException("externalReference no puede ser nulo.");
    }
    if (destinationAccountId == null) {
      throw new IllegalArgumentException("destinationAccountId no puede ser nulo.");
    }
    if (acceptedDestination == null) {
      throw new IllegalArgumentException("acceptedDestination no puede ser nulo.");
    }
    if (principalAmount == null || principalAmount.signum() <= 0) {
      throw new IllegalArgumentException("principalAmount debe ser positivo.");
    }
    if (currency == null) {
      throw new IllegalArgumentException("currency no puede ser nulo.");
    }
    if (purposeCode == null || purposeCode.isBlank()) {
      throw new IllegalArgumentException("purposeCode no puede ser nulo ni vacío.");
    }
    if (authorizationBasis == null) {
      throw new IllegalArgumentException("authorizationBasis no puede ser nulo.");
    }
    if (concepts == null || concepts.isEmpty()) {
      throw new IllegalArgumentException("concepts no puede ser nulo ni vacío.");
    }
    if (externalOperationId == null || externalOperationId.isBlank()) {
      throw new IllegalArgumentException("externalOperationId no puede ser nulo ni vacío.");
    }
    if (messageChannel == null || messageChannel.isBlank()) {
      throw new IllegalArgumentException("messageChannel no puede ser nulo ni vacío.");
    }
  }
}

