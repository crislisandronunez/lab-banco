package com.lab.transferencias.application.command;

import com.lab.transferencias.domain.enums.Currency;
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
    ExternalOperationReference externalOperation,
    ObjectReference orderingParty,
    ObjectReference beneficiaryReference,
    UUID destinationAccountId,
    BigDecimal principalAmount,
    Currency currency,
    String purposeCode,
    ObjectReference messageReference,
    List<ObjectReference> evidenceReferences,
    String receivedContentHash
) {

  public ReceiveIncomingTransferCommand {
    if (context == null) {
      throw new IllegalArgumentException("context no puede ser nulo.");
    }
    if (externalOperation == null) throw new IllegalArgumentException("externalOperation no puede ser nulo.");
    if (orderingParty == null || destinationAccountId == null)
      throw new IllegalArgumentException("orderingParty y destinationAccountId son obligatorios.");
    if (principalAmount == null || principalAmount.signum() <= 0) {
      throw new IllegalArgumentException("principalAmount debe ser positivo.");
    }
    if (currency == null) {
      throw new IllegalArgumentException("currency no puede ser nulo.");
    }
    if (purposeCode == null || purposeCode.isBlank()) {
      throw new IllegalArgumentException("purposeCode no puede ser nulo ni vacío.");
    }
    if (messageReference == null || evidenceReferences == null)
      throw new IllegalArgumentException("messageReference y evidenceReferences son obligatorios.");
    evidenceReferences = List.copyOf(evidenceReferences);
    if (receivedContentHash == null || !receivedContentHash.matches("^[a-fA-F0-9]{64}$"))
      throw new IllegalArgumentException("receivedContentHash debe ser SHA-256 hexadecimal.");
  }
}

