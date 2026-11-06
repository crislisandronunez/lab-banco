package com.lab.transferencias.application.dto.persistence;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento de salida persistido para su publicación confiable después del commit.
 */
public record OutboxEventRecord(
    UUID eventId,
    UUID transferId,
    String eventType,
    String payload,
    String status,
    Instant createdAt,
    Instant publishedAt
) {
}
