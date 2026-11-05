package com.lab.transferencias.application.command;

import com.lab.transferencias.domain.valueobject.ObjectReference;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Comando para tramitar una devolución vinculada a una transferencia anterior.
 * Representa la nueva operación financiera originada por la devolución y su criterio de retorno.
 */
public record ReturnTransferCommand(
    ActionContext context,
    UUID transferId,
    UUID originalTransferId,
    BigDecimal returnAmount,
    String reason,
    ObjectReference returnSource,
    ObjectReference originalActionReference
) {

  public ReturnTransferCommand {
    if (context == null) {
      throw new IllegalArgumentException("context no puede ser nulo.");
    }
    if (transferId == null) {
      throw new IllegalArgumentException("transferId no puede ser nulo.");
    }
    if (originalTransferId == null) {
      throw new IllegalArgumentException("originalTransferId no puede ser nulo.");
    }
    if (returnAmount == null || returnAmount.signum() <= 0) {
      throw new IllegalArgumentException("returnAmount debe ser positivo.");
    }
    if (reason == null || reason.isBlank()) {
      throw new IllegalArgumentException("reason no puede ser nulo ni vacío.");
    }
    if (returnSource == null) {
      throw new IllegalArgumentException("returnSource no puede ser nulo.");
    }
    if (originalActionReference == null) {
      throw new IllegalArgumentException("originalActionReference no puede ser nulo.");
    }
  }
}

