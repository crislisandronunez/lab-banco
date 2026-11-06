package com.lab.transferencias.application.dto.persistence;

import java.time.Instant;
import java.util.UUID;

/**
 * Registro de deduplicación para prevenir repeticiones técnicas o de negocio.
 */
public record IdempotencyRecord(
    UUID id,
    String requestKey,
    UUID actionId,
    String contentHash,
    String status,
    Instant createdAt
) {
}
