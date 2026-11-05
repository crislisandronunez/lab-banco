package com.lab.transferencias.application.exception;

import java.util.UUID;

/**
 * Indica que la transferencia solicitada no existe o no puede recuperarse con la identidad brindada.
 * Se usa para distinguir un recurso ausente dentro del flujo de aplicación.
 */
public class TransferNotFoundException extends ApplicationException {

  public TransferNotFoundException(UUID transferId) {
    super("TRANSFER_NOT_FOUND", "No se encontró la transferencia " + transferId + ".");
  }
}

