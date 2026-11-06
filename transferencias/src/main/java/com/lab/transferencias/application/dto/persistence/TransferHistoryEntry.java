package com.lab.transferencias.application.dto.persistence;

import java.time.Instant;
import java.util.UUID;
import java.util.List;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Entrada del historial de eventos y cambios de una transferencia.
 */
public record TransferHistoryEntry(
    UUID historyId,
    UUID transferId,
    UUID actionId,
    String eventType,
    String summary,
    Instant occurredAt,
    List<ObjectReference> evidenceReferences
) {

  public TransferHistoryEntry {
    if (historyId == null || transferId == null || eventType == null || eventType.isBlank() || occurredAt == null)
      throw new IllegalArgumentException("historyId, transferId, eventType y occurredAt son obligatorios.");
    if (summary == null || summary.isBlank()) throw new IllegalArgumentException("summary no puede estar vacío.");
    evidenceReferences = List.copyOf(evidenceReferences);
  }
}
