package com.lab.transferencias.application.exception;

import java.util.UUID;

/**
 * Indica que una actuación concreta no existe o no puede recuperarse con la identidad indicada.
 * Sirve para separar el problema de la transferencia del problema de la petición específica.
 */
public class TransferActionNotFoundException extends ApplicationException {

  public TransferActionNotFoundException(UUID actionId) {
    super("TRANSFER_ACTION_NOT_FOUND", "No se encontró la actuación " + actionId + ".");
  }
}

