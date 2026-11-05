package com.lab.transferencias.application.command;

import java.util.UUID;

/**
 * Comando para cancelar una transferencia en curso.
 * Define la intención de impedir la ejecución de la instrucción y conserva el motivo de la cancelación.
 */
public record CancelTransferCommand(
    ActionContext context,
    UUID transferId,
    String reason,
    Long expectedVersion
) {

  public CancelTransferCommand {
    if (context == null) {
      throw new IllegalArgumentException("context no puede ser nulo.");
    }
    if (transferId == null) {
      throw new IllegalArgumentException("transferId no puede ser nulo.");
    }
    if (reason == null || reason.isBlank()) {
      throw new IllegalArgumentException("reason no puede ser nulo ni vacío.");
    }
    if (expectedVersion != null && expectedVersion <= 0) {
      throw new IllegalArgumentException("expectedVersion debe ser positivo cuando se informa.");
    }
  }
}

