package com.lab.transferencias.application.command;

import com.lab.transferencias.domain.valueobject.ObjectReference;
import java.util.UUID;

/**
 * Comando para incorporar un resultado externo tardío o recibido por otro canal.
 * Permite cerrar la incertidumbre de una operación con la evidencia de la autoridad externa.
 */
public record IncorporateExternalResultCommand(
    ActionContext context,
    UUID transferId,
    String remoteActionId,
    String resultCode,
    ObjectReference evidenceReference,
    String resultMessage
) {

  public IncorporateExternalResultCommand {
    if (context == null) {
      throw new IllegalArgumentException("context no puede ser nulo.");
    }
    if (transferId == null) {
      throw new IllegalArgumentException("transferId no puede ser nulo.");
    }
    if (remoteActionId == null || remoteActionId.isBlank()) {
      throw new IllegalArgumentException("remoteActionId no puede ser nulo ni vacío.");
    }
    if (resultCode == null || resultCode.isBlank()) {
      throw new IllegalArgumentException("resultCode no puede ser nulo ni vacío.");
    }
    if (evidenceReference == null) {
      throw new IllegalArgumentException("evidenceReference no puede ser nulo.");
    }
    if (resultMessage == null || resultMessage.isBlank()) {
      throw new IllegalArgumentException("resultMessage no puede ser nulo ni vacío.");
    }
  }
}

