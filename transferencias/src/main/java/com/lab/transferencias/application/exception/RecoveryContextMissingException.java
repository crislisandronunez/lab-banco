package com.lab.transferencias.application.exception;

/**
 * Indica que faltan los datos necesarios para recuperar una actuación o un paso remoto pendiente.
 * Se usa cuando la continuidad requiere identidad original y no existe contexto recuperable.
 */
public class RecoveryContextMissingException extends ApplicationException {

  public RecoveryContextMissingException(String message) {
    super("RECOVERY_CONTEXT_MISSING", message);
  }
}

