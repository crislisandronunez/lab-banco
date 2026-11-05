package com.lab.transferencias.application.exception;

/**
 * Indica que una autoridad externa no respondió o quedó indisponible para continuar la coordinación.
 * Permite distinguir una dependencia caída de una decisión de negocio definitiva.
 */
public class ExternalAuthorityUnavailableException extends ApplicationException {

  public ExternalAuthorityUnavailableException(String authority, String message) {
    super("EXTERNAL_AUTHORITY_UNAVAILABLE", "La autoridad " + authority + " no está disponible. " + message);
  }
}

