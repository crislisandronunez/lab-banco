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
    UUID sourceFactId,
    long sourceFactVersion,
    String eventType,
    String payload,
    String status,
    Instant receivedAt
) {

  public InboxEventRecord {
    if (messageId == null || transferId == null || sourceFactId == null)
      throw new IllegalArgumentException("messageId, transferId y sourceFactId son obligatorios.");
    if (sourceFactVersion <= 0) throw new IllegalArgumentException("sourceFactVersion debe ser positiva.");
    if (sourceService == null || sourceService.isBlank() || eventType == null || eventType.isBlank())
      throw new IllegalArgumentException("sourceService y eventType son obligatorios.");
    if (payload == null || status == null || status.isBlank() || receivedAt == null)
      throw new IllegalArgumentException("payload, status y receivedAt son obligatorios.");
  }
}
