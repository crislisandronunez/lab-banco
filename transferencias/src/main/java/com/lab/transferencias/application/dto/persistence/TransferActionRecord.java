package com.lab.transferencias.application.dto.persistence;

import java.time.Instant;
import java.util.UUID;

/**
 * Registro persistente de una actuación asociada a una transferencia.
 */
public record TransferActionRecord(
    UUID actionId,
    UUID transferId,
    UUID operationId,
    String actionType,
    String status,
    Instant createdAt,
    Instant completedAt
) {
}

