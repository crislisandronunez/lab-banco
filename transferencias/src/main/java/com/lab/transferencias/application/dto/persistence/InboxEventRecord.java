package com.lab.transferencias.application.dto.persistence;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento recibido y deduplicado para evitar doble procesamiento.
 */
public record InboxEventRecord(
    UUID messageId,
    UUID transferId,
    String sourceService,
    String eventType,
    String payload,
    String status,
    Instant receivedAt
) {
}
