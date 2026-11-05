package com.lab.transferencias.domain.model;

import com.lab.transferencias.domain.enums.CancellationOutcome;
import com.lab.transferencias.domain.exception.DomainInvariantException;
import com.lab.transferencias.domain.valueobject.AuthorizationBasis;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad subordinada que conserva cada solicitud de cancelación y su resultado.
 */
public class TransferCancellation {

  private final UUID cancellationId;
  private final UUID transferId;
  private final Instant receivedAt;
  private final String reason;
  private final AuthorizationBasis authorizationBasis;
  private final ObjectReference exchangeReference;
  private final UUID cancellationStepId;
  private CancellationOutcome outcome;
  private List<ObjectReference> finalEvidence;
  private final long version;
  private final long lockVersion;

  private TransferCancellation(
      UUID cancellationId,
      UUID transferId,
      Instant receivedAt,
      String reason,
      AuthorizationBasis authorizationBasis,
      ObjectReference exchangeReference,
      UUID cancellationStepId,
      CancellationOutcome outcome,
      List<ObjectReference> finalEvidence,
      long version,
      long lockVersion) {
    if (cancellationId == null) {
      throw new DomainInvariantException("cancellationId es obligatorio.");
    }
    if (transferId == null) {
      throw new DomainInvariantException("transferId es obligatorio.");
    }
    if (receivedAt == null) {
      throw new DomainInvariantException("receivedAt es obligatorio.");
    }
    if (reason == null || reason.isBlank() || reason.length() > 4000) {
      throw new DomainInvariantException("reason debe tener entre 1 y 4000 caracteres.");
    }
    if (authorizationBasis == null) {
      throw new DomainInvariantException("authorizationBasis es obligatorio.");
    }
    if (outcome == null) {
      throw new DomainInvariantException("outcome es obligatorio.");
    }
    if (version < 0) {
      throw new DomainInvariantException("version no puede ser negativa.");
    }
    if (lockVersion < 0) {
      throw new DomainInvariantException("lockVersion no puede ser negativa.");
    }
    this.cancellationId = cancellationId;
    this.transferId = transferId;
    this.receivedAt = receivedAt;
    this.reason = reason;
    this.authorizationBasis = authorizationBasis;
    this.exchangeReference = exchangeReference;
    this.cancellationStepId = cancellationStepId;
    this.outcome = outcome;
    this.finalEvidence = immutableEvidence(finalEvidence);
    this.version = version;
    this.lockVersion = lockVersion;
  }

  public static TransferCancellation create(
      UUID cancellationId,
      UUID transferId,
      Instant receivedAt,
      String reason,
      AuthorizationBasis authorizationBasis,
      ObjectReference exchangeReference,
      UUID cancellationStepId,
      CancellationOutcome outcome,
      List<ObjectReference> finalEvidence,
      long version,
      long lockVersion) {
    return new TransferCancellation(cancellationId, transferId, receivedAt, reason, authorizationBasis,
        exchangeReference, cancellationStepId, outcome, finalEvidence, version, lockVersion);
  }

  public static TransferCancellation restore(
      UUID cancellationId,
      UUID transferId,
      Instant receivedAt,
      String reason,
      AuthorizationBasis authorizationBasis,
      ObjectReference exchangeReference,
      UUID cancellationStepId,
      CancellationOutcome outcome,
      List<ObjectReference> finalEvidence,
      long version,
      long lockVersion) {
    return new TransferCancellation(cancellationId, transferId, receivedAt, reason, authorizationBasis,
        exchangeReference, cancellationStepId, outcome, finalEvidence, version, lockVersion);
  }

  public void registerExternalResponse(CancellationOutcome outcome, List<ObjectReference> evidence) {
    if (outcome == null) {
      throw new DomainInvariantException("outcome es obligatorio.");
    }
    this.outcome = outcome;
    this.finalEvidence = immutableEvidence(evidence);
  }

  public void confirmCancellation() {
    this.outcome = CancellationOutcome.CANCELLED;
  }

  public void recordUncertainOutcome() {
    this.outcome = CancellationOutcome.REQUIRES_RESOLUTION;
  }

  private static List<ObjectReference> immutableEvidence(List<ObjectReference> source) {
    if (source == null || source.isEmpty()) {
      return Collections.emptyList();
    }
    ArrayList<ObjectReference> copy = new ArrayList<>();
    for (ObjectReference item : source) {
      if (item == null) {
        throw new DomainInvariantException("finalEvidence no puede contener elementos nulos.");
      }
      copy.add(item);
    }
    return Collections.unmodifiableList(copy);
  }

  public UUID getCancellationId() { return cancellationId; }
  public UUID getTransferId() { return transferId; }
  public Instant getReceivedAt() { return receivedAt; }
  public String getReason() { return reason; }
  public AuthorizationBasis getAuthorizationBasis() { return authorizationBasis; }
  public ObjectReference getExchangeReference() { return exchangeReference; }
  public UUID getCancellationStepId() { return cancellationStepId; }
  public CancellationOutcome getOutcome() { return outcome; }
  public List<ObjectReference> getFinalEvidence() { return finalEvidence; }
  public long getVersion() { return version; }
  public long getLockVersion() { return lockVersion; }
}
