package com.lab.transferencias.application.query;

import java.util.UUID;

/** Consulta una transferencia por su identidad principal. */
public record GetTransferQuery(UUID transferId) {

  public GetTransferQuery {
    if (transferId == null) throw new IllegalArgumentException("transferId no puede ser nulo.");
  }
}
