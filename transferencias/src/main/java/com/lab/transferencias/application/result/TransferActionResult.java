package com.lab.transferencias.application.result;

import java.time.Instant;
import java.util.UUID;
import java.util.List;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import com.lab.transferencias.domain.valueobject.ConfirmedFact;
import com.lab.transferencias.domain.valueobject.PendingComponent;

/**
 * Resultado de una actuación ejecutada sobre una transferencia.
 * Resume la identidad del resultado y el seguimiento necesario para consultarlo después.
 */
public record TransferActionResult(
    UUID transferId,
    UUID actionId,
    UUID operationId,
    String actionType,
    String status,
    Instant receivedAt,
    Instant completedAt,
    ObjectReference businessReference,
    List<ObjectReference> effectReferences,
    List<ConfirmedFact> confirmedFacts,
    List<PendingComponent> pendingComponents,
    String message
) {

  public TransferActionResult {
    if (actionId == null || operationId == null)
      throw new IllegalArgumentException("actionId y operationId son obligatorios.");
    if (actionType == null || actionType.isBlank() || status == null || status.isBlank())
      throw new IllegalArgumentException("actionType y status son obligatorios.");
    effectReferences = List.copyOf(effectReferences);
    confirmedFacts = List.copyOf(confirmedFacts);
    pendingComponents = List.copyOf(pendingComponents);
  }
}

