package com.lab.transferencias.application.result;

import java.util.List;

/**
 * Resultado paginado de una búsqueda de transferencias por identidad externa.
 * Permite devolver una proyección compacta de coincidencias y metadatos de página.
 */
public record TransferSearchResult(
    List<TransferView> items,
    int page,
    int size,
    long totalElements
) {

  public TransferSearchResult {
    if (items == null) {
      throw new IllegalArgumentException("items no puede ser nulo.");
    }
    if (page < 0) {
      throw new IllegalArgumentException("page no puede ser negativo.");
    }
    if (size <= 0) {
      throw new IllegalArgumentException("size debe ser positivo.");
    }
  }
}

