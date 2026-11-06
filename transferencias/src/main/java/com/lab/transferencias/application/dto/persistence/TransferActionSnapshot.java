package com.lab.transferencias.application.dto.persistence;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import com.lab.transferencias.domain.valueobject.ConfirmedFact;
import com.lab.transferencias.domain.valueobject.PendingComponent;

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
    ObjectReference businessReference,
    List<ObjectReference> effectReferences,
    List<ConfirmedFact> confirmedFacts,
    List<PendingComponent> pendingComponents,
    Instant receivedAt,
    Instant completedAt
) {

  public TransferActionSnapshot {
    if (actionId == null || operationId == null)
      throw new IllegalArgumentException("actionId y operationId son obligatorios.");
    if (actionType == null || actionType.isBlank() || status == null || status.isBlank())
      throw new IllegalArgumentException("actionType y status son obligatorios.");
    effectReferences = List.copyOf(effectReferences);
    confirmedFacts = List.copyOf(confirmedFacts);
    pendingComponents = List.copyOf(pendingComponents);
  }
}

