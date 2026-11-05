package com.lab.transferencias.application.exception;

import java.util.UUID;

/**
 * Indica que la actuación solicitada no está autorizada para el actor o servicio involucrado.
 * Evita que una operación continúe sin las facultades apropiadas.
 */
public class TransferAccessDeniedException extends ApplicationException {

  public TransferAccessDeniedException(UUID transferId, String actor) {
    super("TRANSFER_ACCESS_DENIED", "No tiene permisos para operar la transferencia " + transferId + " como " + actor + ".");
  }
}

