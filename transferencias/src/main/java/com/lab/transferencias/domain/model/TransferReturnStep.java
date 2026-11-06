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
 * Conserva la intención y recuperación de una actuación remota asociada a una devolución.
 */
public class TransferReturnStep {

  private final UUID stepId;
  private final UUID returnId;
  private final String componentCode;
  private final String responsibleService;
  private final UUID remoteActionId;
  private UUID effectId;
  private StructuredContent requestedContent;
  private RemoteStepStatus status;
  private ObjectReference resultReference;
  private StructuredContent resultPayload;
  private List<ObjectReference> evidenceReferences;
  private Instant lastCheckedAt;
  private Instant nextCheckAt;

  private TransferReturnStep(
      UUID stepId,
      UUID returnId,
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
    if (returnId == null) {
      throw new DomainInvariantException("returnId es obligatorio.");
    }
    if (componentCode == null || componentCode.isBlank()) {
      throw new DomainInvariantException("componentCode no puede ser nulo ni vacío.");
    }
    if (componentCode.length() > 80) throw new DomainInvariantException("componentCode no puede exceder 80 caracteres.");
    if (responsibleService == null || responsibleService.isBlank()) {
      throw new DomainInvariantException("responsibleService no puede ser nulo ni vacío.");
    }
    if (responsibleService.length() > 80) throw new DomainInvariantException("responsibleService no puede exceder 80 caracteres.");
    if (remoteActionId == null) {
      throw new DomainInvariantException("remoteActionId es obligatorio.");
    }
    if (requestedContent == null) {
      throw new DomainInvariantException("requestedContent es obligatorio.");
    }
    if (status == null) {
      throw new DomainInvariantException("status es obligatorio.");
    }
    if (status == RemoteStepStatus.CONFIRMED && (resultReference == null || resultPayload == null || evidenceReferences == null || evidenceReferences.isEmpty()))
      throw new DomainInvariantException("Un paso confirmado requiere resultado y evidencia.");
    this.stepId = stepId;
    this.returnId = returnId;
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

  public static TransferReturnStep create(
      UUID stepId,
      UUID returnId,
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
    return new TransferReturnStep(stepId, returnId, componentCode, responsibleService, remoteActionId,
        effectId, requestedContent, status, resultReference, resultPayload, evidenceReferences,
        lastCheckedAt, nextCheckAt);
  }

  public static TransferReturnStep restore(
      UUID stepId,
      UUID returnId,
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
    return create(stepId, returnId, componentCode, responsibleService, remoteActionId, effectId,
        requestedContent, status, resultReference, resultPayload, evidenceReferences, lastCheckedAt,
        nextCheckAt);
  }

  public void registerIntent(StructuredContent requestedContent, UUID effectId) {
    if (requestedContent == null) {
      throw new DomainInvariantException("requestedContent es obligatorio.");
    }
    if (this.effectId != null && !this.effectId.equals(effectId)) {
      throw new DomainInvariantException("effectId no puede cambiar durante la recuperación del paso.");
    }
    if (!this.requestedContent.contentHash().equals(requestedContent.contentHash()))
      throw new DomainInvariantException("El contenido de la intención remota no puede cambiar.");
    if (this.effectId == null) this.effectId = effectId;
    if (this.status == RemoteStepStatus.CONFIRMED)
      throw new DomainInvariantException("No se puede reabrir un paso remoto confirmado.");
    if (this.status == RemoteStepStatus.REJECTED || this.status == RemoteStepStatus.REQUIRES_REVIEW)
      throw new DomainInvariantException("Un paso remoto definitivo requiere una nueva actuación para continuar.");
    this.status = RemoteStepStatus.PENDING;
    this.lastCheckedAt = Instant.now();
  }

  public void recordResult(ObjectReference resultReference, StructuredContent resultPayload,
      List<ObjectReference> evidenceReferences) {
    if (status == RemoteStepStatus.CONFIRMED && (!Objects.equals(this.resultReference, resultReference)
        || !Objects.equals(this.resultPayload, resultPayload)))
      throw new DomainInvariantException("El resultado remoto confirmado es inmutable.");
    if (resultReference == null) {
      throw new DomainInvariantException("resultReference es obligatorio.");
    }
    if (resultPayload == null) {
      throw new DomainInvariantException("resultPayload es obligatorio.");
    }
    if (evidenceReferences == null || evidenceReferences.isEmpty())
      throw new DomainInvariantException("Un resultado confirmado requiere evidencia verificable.");
    if (status == RemoteStepStatus.REJECTED || status == RemoteStepStatus.REQUIRES_REVIEW)
      throw new DomainInvariantException("El estado definitivo del paso no puede reemplazarse sin revisión.");
    this.resultReference = resultReference;
    this.resultPayload = resultPayload;
    this.evidenceReferences = immutableReferences(evidenceReferences);
    this.status = RemoteStepStatus.CONFIRMED;
    this.lastCheckedAt = Instant.now();
    this.nextCheckAt = null;
  }

  public void markUncertain() {
    if (status == RemoteStepStatus.CONFIRMED || status == RemoteStepStatus.REJECTED)
      throw new DomainInvariantException("Un resultado definitivo no puede volver a incierto.");
    if (status != RemoteStepStatus.PENDING && status != RemoteStepStatus.UNCERTAIN)
      throw new DomainInvariantException("Solo una actuación remota registrada puede quedar incierta.");
    this.status = RemoteStepStatus.UNCERTAIN;
    this.lastCheckedAt = Instant.now();
    this.nextCheckAt = this.lastCheckedAt.plusSeconds(300);
  }

  public void scheduleNextCheck(Instant nextCheckAt) {
    if (nextCheckAt == null) {
      throw new DomainInvariantException("nextCheckAt es obligatorio.");
    }
    if (status != RemoteStepStatus.UNCERTAIN)
      throw new DomainInvariantException("Solo un resultado incierto requiere programación de consulta.");
    if (lastCheckedAt != null && nextCheckAt.isBefore(lastCheckedAt))
      throw new DomainInvariantException("La próxima consulta no puede preceder a la última comprobación.");
    this.nextCheckAt = nextCheckAt;
    this.lastCheckedAt = Instant.now();
  }

  public void recordDefinitiveRejection(ObjectReference evidence) {
    if (evidence == null) throw new DomainInvariantException("El rechazo requiere evidencia.");
    if (status == RemoteStepStatus.CONFIRMED) throw new DomainInvariantException("Un paso confirmado no puede rechazarse.");
    if (status != RemoteStepStatus.PENDING && status != RemoteStepStatus.UNCERTAIN
        && status != RemoteStepStatus.REJECTED)
      throw new DomainInvariantException("Solo una actuación remota registrada puede rechazarse.");
    this.resultReference = evidence;
    this.status = RemoteStepStatus.REJECTED;
    this.lastCheckedAt = Instant.now();
    this.nextCheckAt = null;
  }

  public void recordRequiresReview(ObjectReference evidence) {
    if (evidence == null) throw new DomainInvariantException("La revisión requiere referencia de evidencia.");
    if (status == RemoteStepStatus.CONFIRMED) throw new DomainInvariantException("Un paso confirmado no puede pasar a revisión.");
    resultReference = evidence;
    status = RemoteStepStatus.REQUIRES_REVIEW;
    lastCheckedAt = Instant.now();
    nextCheckAt = null;
  }

  public void scheduleFiveMinutes() { scheduleNextCheck(Instant.now().plusSeconds(300)); }
  public void scheduleThirtyMinutes() { scheduleNextCheck(Instant.now().plusSeconds(1800)); }
  public void scheduleNextOperationalDay(Instant now) {
    if (now == null) throw new DomainInvariantException("now es obligatorio.");
    java.time.ZoneId zone = java.time.ZoneId.of("America/Argentina/Buenos_Aires");
    java.time.LocalDate next = now.atZone(zone).toLocalDate().plusDays(1);
    scheduleNextCheck(next.atStartOfDay(zone).toInstant());
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
  public UUID getReturnId() { return returnId; }
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
