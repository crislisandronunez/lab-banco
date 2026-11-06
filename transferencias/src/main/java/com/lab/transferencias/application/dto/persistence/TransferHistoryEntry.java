package com.lab.transferencias.application.dto.persistence;

import java.time.Instant;
import java.util.UUID;

/**
 * Entrada del historial de eventos y cambios de una transferencia.
 */
public record TransferHistoryEntry(
    UUID historyId,
    UUID transferId,
    UUID actionId,
    String eventType,
    String summary,
    Instant occurredAt
) {
}
