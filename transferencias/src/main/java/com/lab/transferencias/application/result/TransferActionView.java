package com.lab.transferencias.application.result;

import java.time.Instant;
import java.util.UUID;
import java.util.List;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import com.lab.transferencias.domain.valueobject.ConfirmedFact;
import com.lab.transferencias.domain.valueobject.PendingComponent;

/**
 * Vista autorizada de una actuación persistente para consulta.
 * Resume la identidad de la operación, el estado y la evidencia vinculada.
 */
public record TransferActionView(
    UUID actionId,
    UUID operationId,
    UUID transferId,
    String actionType,
    String status,
    Instant receivedAt,
    Instant createdAt,
    Instant completedAt,
    List<ObjectReference> effectReferences,
    List<ConfirmedFact> confirmedFacts,
    List<PendingComponent> pendingComponents,
    String message
) {

  public TransferActionView {
    effectReferences = List.copyOf(effectReferences);
    confirmedFacts = List.copyOf(confirmedFacts);
    pendingComponents = List.copyOf(pendingComponents);
  }
}

