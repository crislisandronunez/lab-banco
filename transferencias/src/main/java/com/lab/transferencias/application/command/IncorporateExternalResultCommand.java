package com.lab.transferencias.application.command;

import com.lab.transferencias.domain.valueobject.ObjectReference;
import com.lab.transferencias.domain.valueobject.ConfirmedFact;
import java.util.UUID;

/**
 * Comando para incorporar un resultado externo tardío o recibido por otro canal.
 * Permite cerrar la incertidumbre de una operación con la evidencia de la autoridad externa.
 */
public record IncorporateExternalResultCommand(
    ActionContext context,
    UUID transferId,
    ObjectReference sourceReference,
    UUID sourceFactId,
    long sourceFactVersion,
    ConfirmedFact fact,
    UUID relatedActionId,
    UUID messageId
) {

  public IncorporateExternalResultCommand {
    if (context == null) {
      throw new IllegalArgumentException("context no puede ser nulo.");
    }
    if (transferId == null) {
      throw new IllegalArgumentException("transferId no puede ser nulo.");
    }
    if (sourceReference == null || sourceFactId == null || fact == null || messageId == null)
      throw new IllegalArgumentException("sourceReference, sourceFactId, fact y messageId son obligatorios.");
    if (sourceFactVersion <= 0) throw new IllegalArgumentException("sourceFactVersion debe ser positiva.");
  }
}

