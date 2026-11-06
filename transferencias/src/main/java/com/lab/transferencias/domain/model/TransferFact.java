package com.lab.transferencias.domain.model;

import com.lab.transferencias.domain.enums.ConfirmedFactType;
import com.lab.transferencias.domain.exception.DomainInvariantException;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Registra un hecho confirmado de la transferencia de manera inmutable y deduplicada.
 */
public class TransferFact {

  private final UUID factId;
  private final UUID transferId;
  private final ConfirmedFactType factType;
  private final String sourceService;
  private final UUID sourceFactId;
  private final long sourceFactVersion;
  private final Instant occurredAt;
  private final ObjectReference sourceReference;
  private final List<ObjectReference> evidenceReferences;

  private TransferFact(
      UUID factId,
      UUID transferId,
      ConfirmedFactType factType,
      String sourceService,
      UUID sourceFactId,
      long sourceFactVersion,
      Instant occurredAt,
      ObjectReference sourceReference,
      List<ObjectReference> evidenceReferences) {
    if (factId == null) {
      throw new DomainInvariantException("factId es obligatorio.");
    }
    if (transferId == null) {
      throw new DomainInvariantException("transferId es obligatorio.");
    }
    if (factType == null) {
      throw new DomainInvariantException("factType es obligatorio.");
    }
    if (sourceService == null || sourceService.isBlank()) {
      throw new DomainInvariantException("sourceService no puede ser nulo ni vacío.");
    }
    if (sourceService.length() > 80) throw new DomainInvariantException("sourceService no puede exceder 80 caracteres.");
    if (sourceFactId == null) {
      throw new DomainInvariantException("sourceFactId es obligatorio.");
    }
    if (sourceFactVersion <= 0) {
      throw new DomainInvariantException("sourceFactVersion debe ser positivo.");
    }
    if (occurredAt == null) {
      throw new DomainInvariantException("occurredAt es obligatorio.");
    }
    if (sourceReference == null) {
      throw new DomainInvariantException("sourceReference es obligatorio.");
    }
    this.factId = factId;
    this.transferId = transferId;
    this.factType = factType;
    this.sourceService = sourceService;
    this.sourceFactId = sourceFactId;
    this.sourceFactVersion = sourceFactVersion;
    this.occurredAt = occurredAt;
    this.sourceReference = sourceReference;
    this.evidenceReferences = immutableReferences(evidenceReferences);
    if (this.evidenceReferences.isEmpty())
      throw new DomainInvariantException("Un hecho de origen requiere evidencia.");
  }

  public static TransferFact create(
      UUID factId,
      UUID transferId,
      ConfirmedFactType factType,
      String sourceService,
      UUID sourceFactId,
      long sourceFactVersion,
      Instant occurredAt,
      ObjectReference sourceReference,
      List<ObjectReference> evidenceReferences) {
    return new TransferFact(factId, transferId, factType, sourceService, sourceFactId, sourceFactVersion,
        occurredAt, sourceReference, evidenceReferences);
  }

  public static TransferFact restore(
      UUID factId,
      UUID transferId,
      ConfirmedFactType factType,
      String sourceService,
      UUID sourceFactId,
      long sourceFactVersion,
      Instant occurredAt,
      ObjectReference sourceReference,
      List<ObjectReference> evidenceReferences) {
    return create(factId, transferId, factType, sourceService, sourceFactId, sourceFactVersion,
        occurredAt, sourceReference, evidenceReferences);
  }

  public boolean matchesOriginIdentity(String sourceService, UUID sourceFactId, long sourceFactVersion) {
    return Objects.equals(this.sourceService, sourceService)
        && Objects.equals(this.sourceFactId, sourceFactId)
        && this.sourceFactVersion == sourceFactVersion;
  }

  public boolean sameOriginContent(TransferFact other) {
    return other != null && matchesOriginIdentity(other.sourceService, other.sourceFactId, other.sourceFactVersion)
        && transferId.equals(other.transferId) && factType == other.factType
        && occurredAt.equals(other.occurredAt) && sourceReference.equals(other.sourceReference)
        && evidenceReferences.equals(other.evidenceReferences);
  }

  private static List<ObjectReference> immutableReferences(List<ObjectReference> source) {
    if (source == null || source.isEmpty()) {
      return Collections.emptyList();
    }
    ArrayList<ObjectReference> copy = new ArrayList<>();
    for (ObjectReference item : source) {
      if (item == null) {
        throw new DomainInvariantException("evidenceReferences no puede contener elementos nulos.");
      }
      copy.add(item);
    }
    return Collections.unmodifiableList(copy);
  }

  public UUID getFactId() { return factId; }
  public UUID getTransferId() { return transferId; }
  public ConfirmedFactType getFactType() { return factType; }
  public String getSourceService() { return sourceService; }
  public UUID getSourceFactId() { return sourceFactId; }
  public long getSourceFactVersion() { return sourceFactVersion; }
  public Instant getOccurredAt() { return occurredAt; }
  public ObjectReference getSourceReference() { return sourceReference; }
  public List<ObjectReference> getEvidenceReferences() { return evidenceReferences; }
}
