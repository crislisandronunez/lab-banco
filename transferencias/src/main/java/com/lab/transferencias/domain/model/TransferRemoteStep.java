package com.lab.transferencias.domain.model;

import com.lab.transferencias.domain.enums.RemoteStepStatus;
import com.lab.transferencias.domain.exception.DomainInvariantException;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import com.lab.transferencias.domain.valueobject.StructuredContent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Conserva la intención y la recuperación de una actuación remota asociada a la transferencia.
 */
public class TransferRemoteStep {

  private final UUID stepId;
  private final UUID transferId;
  private final String componentCode;
  private final String responsibleService;
  private final UUID remoteActionId;
  private final UUID effectId;
  private final StructuredContent requestedContent;
  private RemoteStepStatus status;
  private ObjectReference resultReference;
  private StructuredContent resultPayload;
  private List<ObjectReference> evidenceReferences;
  private Instant lastCheckedAt;
  private Instant nextCheckAt;

  private TransferRemoteStep(
      UUID stepId,
      UUID transferId,
      String componentCode,
      String responsibleService,
      UUID remoteActionId,
      UUID effectId,
      StructuredContent requestedContent,
      RemoteStepStatus status,
      ObjectReference resultReference,
      StructuredContent resultPayload,
      List<ObjectReference> evidenceReferences,
      Instant lastCheckedAt,
      Instant nextCheckAt) {
    if (stepId == null) {
      throw new DomainInvariantException("stepId es obligatorio.");
    }
    if (transferId == null) {
      throw new DomainInvariantException("transferId es obligatorio.");
    }
    if (componentCode == null || componentCode.isBlank()) {
      throw new DomainInvariantException("componentCode no puede ser nulo ni vacío.");
    }
    if (responsibleService == null || responsibleService.isBlank()) {
      throw new DomainInvariantException("responsibleService no puede ser nulo ni vacío.");
    }
    if (remoteActionId == null) {
      throw new DomainInvariantException("remoteActionId es obligatorio.");
    }
    if (requestedContent == null) {
      throw new DomainInvariantException("requestedContent es obligatorio.");
    }
    if (status == null) {
      throw new DomainInvariantException("status es obligatorio.");
    }
    this.stepId = stepId;
    this.transferId = transferId;
    this.componentCode = componentCode;
    this.responsibleService = responsibleService;
    this.remoteActionId = remoteActionId;
    this.effectId = effectId;
    this.requestedContent = requestedContent;
    this.status = status;
    this.resultReference = resultReference;
    this.resultPayload = resultPayload;
    this.evidenceReferences = immutableReferences(evidenceReferences);
    this.lastCheckedAt = lastCheckedAt;
    this.nextCheckAt = nextCheckAt;
  }

  public static TransferRemoteStep create(
      UUID stepId,
      UUID transferId,
      String componentCode,
      String responsibleService,
      UUID remoteActionId,
      UUID effectId,
      StructuredContent requestedContent,
      RemoteStepStatus status,
      ObjectReference resultReference,
      StructuredContent resultPayload,
      List<ObjectReference> evidenceReferences,
      Instant lastCheckedAt,
      Instant nextCheckAt) {
    return new TransferRemoteStep(stepId, transferId, componentCode, responsibleService, remoteActionId,
        effectId, requestedContent, status, resultReference, resultPayload, evidenceReferences,
        lastCheckedAt, nextCheckAt);
  }

  public static TransferRemoteStep restore(
      UUID stepId,
      UUID transferId,
      String componentCode,
      String responsibleService,
      UUID remoteActionId,
      UUID effectId,
      StructuredContent requestedContent,
      RemoteStepStatus status,
      ObjectReference resultReference,
      StructuredContent resultPayload,
      List<ObjectReference> evidenceReferences,
      Instant lastCheckedAt,
      Instant nextCheckAt) {
    return create(stepId, transferId, componentCode, responsibleService, remoteActionId, effectId,
        requestedContent, status, resultReference, resultPayload, evidenceReferences, lastCheckedAt,
        nextCheckAt);
  }

  public void registerIntent(StructuredContent requestedContent, UUID effectId) {
    if (requestedContent == null) {
      throw new DomainInvariantException("requestedContent es obligatorio.");
    }
    if (this.effectId != null && effectId != null && !this.effectId.equals(effectId)) {
      throw new DomainInvariantException("effectId no puede cambiar durante la recuperación del paso.");
    }
    this.status = RemoteStepStatus.PENDING;
    this.lastCheckedAt = Instant.now();
  }

  public void recordResult(ObjectReference resultReference, StructuredContent resultPayload,
      List<ObjectReference> evidenceReferences) {
    if (resultReference == null) {
      throw new DomainInvariantException("resultReference es obligatorio.");
    }
    if (resultPayload == null) {
      throw new DomainInvariantException("resultPayload es obligatorio.");
    }
    this.resultReference = resultReference;
    this.resultPayload = resultPayload;
    this.evidenceReferences = immutableReferences(evidenceReferences);
    this.status = RemoteStepStatus.CONFIRMED;
    this.lastCheckedAt = Instant.now();
  }

  public void markUncertain() {
    this.status = RemoteStepStatus.UNCERTAIN;
    this.lastCheckedAt = Instant.now();
  }

  public void scheduleNextCheck(Instant nextCheckAt) {
    if (nextCheckAt == null) {
      throw new DomainInvariantException("nextCheckAt es obligatorio.");
    }
    this.nextCheckAt = nextCheckAt;
    this.lastCheckedAt = Instant.now();
  }

  public boolean hasSameRemoteIdentity(UUID remoteActionId, String responsibleService) {
    return Objects.equals(this.remoteActionId, remoteActionId)
        && Objects.equals(this.responsibleService, responsibleService);
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

  public UUID getStepId() { return stepId; }
  public UUID getTransferId() { return transferId; }
  public String getComponentCode() { return componentCode; }
  public String getResponsibleService() { return responsibleService; }
  public UUID getRemoteActionId() { return remoteActionId; }
  public UUID getEffectId() { return effectId; }
  public StructuredContent getRequestedContent() { return requestedContent; }
  public RemoteStepStatus getStatus() { return status; }
  public ObjectReference getResultReference() { return resultReference; }
  public StructuredContent getResultPayload() { return resultPayload; }
  public List<ObjectReference> getEvidenceReferences() { return evidenceReferences; }
  public Instant getLastCheckedAt() { return lastCheckedAt; }
  public Instant getNextCheckAt() { return nextCheckAt; }
}
