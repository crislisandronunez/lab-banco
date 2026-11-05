package com.lab.transferencias.domain.valueobject;

import com.lab.transferencias.domain.exception.DomainInvariantException;

/**
 * Conserva una petición o respuesta remota con un esquema identificado y contenido inmutable.
 */
public record StructuredContent(
    String schemaCode,
    long schemaVersion,
    String contentHash,
    Object data
) {

  public StructuredContent {
    if (schemaCode == null || schemaCode.isBlank()) {
      throw new DomainInvariantException("schemaCode no puede ser nulo ni vacío.");
    }
    if (schemaVersion <= 0) {
      throw new DomainInvariantException("schemaVersion debe ser positivo.");
    }
    if (contentHash == null || !contentHash.matches("^[a-fA-F0-9]{64}$")) {
      throw new DomainInvariantException("contentHash debe ser un SHA-256 hexadecimal de 64 caracteres.");
    }
    if (data == null) {
      throw new DomainInvariantException("data es obligatorio.");
    }
  }
}
