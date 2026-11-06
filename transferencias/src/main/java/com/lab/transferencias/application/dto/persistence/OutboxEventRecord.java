package com.lab.transferencias.application.dto.persistence;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento de salida persistido para su publicación confiable después del commit.
 */
public record OutboxEventRecord(
    UUID eventId,
    UUID transferId,
    long aggregateVersion,
    String eventType,
    String payload,
    String status,
    Instant createdAt,
    Instant publishedAt
) {

  public OutboxEventRecord {
    if (eventId == null || transferId == null) throw new IllegalArgumentException("eventId y transferId son obligatorios.");
    if (aggregateVersion <= 0) throw new IllegalArgumentException("aggregateVersion debe ser positiva.");
    if (eventType == null || eventType.isBlank() || payload == null || status == null || status.isBlank())
      throw new IllegalArgumentException("eventType, payload y status son obligatorios.");
    if (createdAt == null) throw new IllegalArgumentException("createdAt no puede ser nulo.");
  }
}
