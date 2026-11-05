package com.lab.transferencias.application.exception;

import java.util.UUID;

/**
 * Indica que la transferencia fue modificada por otra operación y la versión esperada ya no coincide.
 * Protege la concurrencia y evita pérdidas de actualización.
 */
public class TransferVersionConflictException extends ApplicationException {

  public TransferVersionConflictException(UUID transferId, long expectedVersion, long actualVersion) {
    super("TRANSFER_VERSION_CONFLICT",
        "La transferencia " + transferId + " fue modificada. Esperada versión " + expectedVersion + " pero actual es " + actualVersion + ".");
  }
}

