package com.lab.transferencias.application.command;

import java.util.UUID;
import com.lab.transferencias.domain.valueobject.AuthorizationBasis;

/**
 * Comando para cancelar una transferencia en curso.
 * Define la intención de impedir la ejecución de la instrucción y conserva el motivo de la cancelación.
 */
public record CancelTransferCommand(
    ActionContext context,
    UUID transferId,
    String reason,
    AuthorizationBasis authorizationBasis
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
    if (authorizationBasis == null) {
      throw new IllegalArgumentException("authorizationBasis no puede ser nulo.");
    }
  }
}

