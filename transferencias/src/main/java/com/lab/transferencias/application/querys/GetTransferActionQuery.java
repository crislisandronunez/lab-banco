package com.lab.transferencias.application.querys;

import java.util.UUID;

/**
 * Consulta para recuperar el detalle de una actuación persistente por su identificador.
 * Permite revisar la petición original y su resultado asociado.
 */
public record GetTransferActionQuery(
    UUID actionId,
    String actorId,
    String permissionScope
) {

  public GetTransferActionQuery {
    if (actionId == null) {
      throw new IllegalArgumentException("actionId no puede ser nulo.");
    }
    if (actorId == null || actorId.isBlank()) {
      throw new IllegalArgumentException("actorId no puede ser nulo ni vacío.");
    }
    if (permissionScope == null || permissionScope.isBlank()) {
      throw new IllegalArgumentException("permissionScope no puede ser nulo ni vacío.");
    }
  }
}

