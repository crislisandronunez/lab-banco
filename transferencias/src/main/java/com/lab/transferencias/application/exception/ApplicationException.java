package com.lab.transferencias.application.exception;

import java.util.UUID;

/**
 * Excepción base para los errores de coordinación de la capa de aplicación.
 * Permite distinguir fallas funcionales de la lógica del dominio y conservar contexto útil para diagnóstico.
 */
public abstract class ApplicationException extends RuntimeException {

  private final String errorCode;
  private final UUID actionId;
  private final UUID transferId;

  protected ApplicationException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
    this.actionId = null;
    this.transferId = null;
  }

  protected ApplicationException(String errorCode, String message, UUID actionId, UUID transferId, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
    this.actionId = actionId;
    this.transferId = transferId;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public UUID getActionId() {
    return actionId;
  }

  public UUID getTransferId() {
    return transferId;
  }
}

