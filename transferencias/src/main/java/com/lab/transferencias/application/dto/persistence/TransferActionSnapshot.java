package com.lab.transferencias.application.dto.persistence;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Snapshot persistente de una actuación con su resultado original y referencias de seguimiento.
 */
public record TransferActionSnapshot(
    UUID actionId,
    UUID transferId,
    UUID operationId,
    String actionType,
    String status,
    String resultType,
    String resultPayload,
    List<String> references,
    Instant createdAt,
    Instant completedAt
) {
}

