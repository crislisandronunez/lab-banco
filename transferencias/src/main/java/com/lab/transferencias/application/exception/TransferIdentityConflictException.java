package com.lab.transferencias.application.exception;

import java.util.UUID;

/**
 * Indica que la identidad empresarial de la transferencia no coincide con la instrucción recibida.
 * Se usa para rechazar datos que intentan reutilizar la misma operación con contenido distinto.
 */
public class TransferIdentityConflictException extends ApplicationException {

  public TransferIdentityConflictException(UUID transferId, String message) {
    super("TRANSFER_IDENTITY_CONFLICT", message + " (transferId=" + transferId + ")");
  }
}

