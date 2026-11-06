package com.lab.transferencias.application.command;

import com.lab.transferencias.domain.valueobject.ObjectReference;
import com.lab.transferencias.domain.valueobject.ReturnDestination;
import com.lab.transferencias.domain.valueobject.AuthorizationBasis;
import com.lab.transferencias.domain.enums.Currency;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Comando para tramitar una devolución vinculada a una transferencia anterior.
 * Representa la nueva operación financiera originada por la devolución y su criterio de retorno.
 */
public record ReturnTransferCommand(
    ActionContext context,
    UUID transferId,
    UUID returnEffectId,
    BigDecimal amount,
    Currency currency,
    String reasonCode,
    ReturnDestination returnDestination,
    AuthorizationBasis authorizationBasis,
    List<ObjectReference> relatedReturns
) {

  public ReturnTransferCommand {
    if (context == null) {
      throw new IllegalArgumentException("context no puede ser nulo.");
    }
    if (transferId == null) {
      throw new IllegalArgumentException("transferId no puede ser nulo.");
    }
    if (returnEffectId == null) {
      throw new IllegalArgumentException("returnEffectId no puede ser nulo.");
    }
    if (amount == null || amount.signum() <= 0) {
      throw new IllegalArgumentException("amount debe ser positivo.");
    }
    if (currency == null) {
      throw new IllegalArgumentException("currency no puede ser nula.");
    }
    if (reasonCode == null || reasonCode.isBlank()) {
      throw new IllegalArgumentException("reasonCode no puede ser nulo ni vacío.");
    }
    if (returnDestination == null || authorizationBasis == null) {
      throw new IllegalArgumentException("returnDestination y authorizationBasis son obligatorios.");
    }
    relatedReturns = relatedReturns == null ? List.of() : List.copyOf(relatedReturns);
  }
}

