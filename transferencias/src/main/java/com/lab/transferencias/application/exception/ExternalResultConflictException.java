package com.lab.transferencias.application.exception;

/**
 * Indica que una respuesta o evidencia externa contradice el resultado esperado por este caso de uso.
 * Permite detener una ejecución cuando la autoridad informa algo incompatible con la instrucción.
 */
public class ExternalResultConflictException extends ApplicationException {

  public ExternalResultConflictException(String authority, String message) {
    super("EXTERNAL_RESULT_CONFLICT", "Resultado contradictorio de " + authority + ": " + message);
  }
}

