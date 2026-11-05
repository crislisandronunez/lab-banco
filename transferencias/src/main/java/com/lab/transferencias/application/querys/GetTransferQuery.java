package com.lab.transferencias.application.querys;

import java.util.UUID;

/**
 * Consulta para recuperar una transferencia por su identidad principal.
 * Permite obtener el estado y la vista autorizada de una instrucción determinada.
 */
public record GetTransferQuery(
    UUID transferId,
    String actorId,
    String permissionScope
) {

  public GetTransferQuery {
    if (transferId == null) {
      throw new IllegalArgumentException("transferId no puede ser nulo.");
    }
    if (actorId == null || actorId.isBlank()) {
      throw new IllegalArgumentException("actorId no puede ser nulo ni vacío.");
    }
    if (permissionScope == null || permissionScope.isBlank()) {
      throw new IllegalArgumentException("permissionScope no puede ser nulo ni vacío.");
    }
  }
}

