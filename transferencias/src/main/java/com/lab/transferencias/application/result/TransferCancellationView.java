package com.lab.transferencias.application.result;

import com.lab.transferencias.domain.enums.CancellationOutcome;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Datos de consulta de una solicitud de cancelación. */
public record TransferCancellationView(
    UUID cancellationId,
    Instant receivedAt,
    String reason,
    CancellationOutcome outcome,
    List<ObjectReference> evidence
) {

  public TransferCancellationView {
    evidence = List.copyOf(evidence);
  }
}
