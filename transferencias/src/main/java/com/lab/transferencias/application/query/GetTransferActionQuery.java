package com.lab.transferencias.application.query;

import java.util.UUID;

/** Consulta una actuación por su identidad persistente. */
public record GetTransferActionQuery(UUID actionId) {

  public GetTransferActionQuery {
    if (actionId == null) throw new IllegalArgumentException("actionId no puede ser nulo.");
  }
}
