package com.lab.transferencias.application.exception;

import java.util.UUID;

/**
 * Indica que falta o es inválido el contexto de una actuación requerida por el caso de uso.
 * Se usa para impedir que una operación avance sin identidad, causa o evidencia suficientes.
 */
public class InvalidActionContextException extends ApplicationException {

  public InvalidActionContextException(String message) {
    super("INVALID_ACTION_CONTEXT", message);
  }

  public InvalidActionContextException(String message, UUID actionId, UUID transferId, Throwable cause) {
    super("INVALID_ACTION_CONTEXT", message, actionId, transferId, cause);
  }
}

