package com.lab.transferencias.application.dto.persistence;

import java.time.Instant;
import java.util.UUID;

/**
 * Registro de deduplicación para prevenir repeticiones técnicas o de negocio.
 */
public record IdempotencyRecord(
    UUID id,
    String scope,
    String requestKey,
    UUID actionId,
    String contentHash,
    String status,
    Instant createdAt
) {

  public IdempotencyRecord {
    if (id == null || actionId == null || createdAt == null)
      throw new IllegalArgumentException("id, actionId y createdAt son obligatorios.");
    if (scope == null || scope.isBlank() || requestKey == null || requestKey.isBlank())
      throw new IllegalArgumentException("scope y requestKey son obligatorios.");
    if (contentHash == null || !contentHash.matches("^[a-fA-F0-9]{64}$"))
      throw new IllegalArgumentException("contentHash debe ser SHA-256 hexadecimal.");
    if (status == null || status.isBlank()) throw new IllegalArgumentException("status no puede estar vacío.");
  }
}
