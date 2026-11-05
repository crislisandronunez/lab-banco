package com.lab.transferencias.domain.exception;

/**
 * Excepción utilizada para indicar que un valor de dominio no cumple sus invariantes.
 */
public class DomainInvariantException extends IllegalArgumentException {

  public DomainInvariantException(String message) {
    super(message);
  }

  public DomainInvariantException(String message, Throwable cause) {
    super(message, cause);
  }
}

