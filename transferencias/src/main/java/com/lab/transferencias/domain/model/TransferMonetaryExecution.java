package com.lab.transferencias.domain.model;

import com.lab.transferencias.domain.enums.MonetaryExecutionStatus;
import com.lab.transferencias.domain.exception.DomainInvariantException;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import com.lab.transferencias.domain.valueobject.StructuredContent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa la ejecución monetaria solicitada a Fondos y su resultado verificable.
 */
public class TransferMonetaryExecution {

  private final UUID transferId;
  private final String fundsService;
  private final UUID remoteActionId;
  private UUID movementGroupId;
  private UUID debitEffectId;
  private UUID creditEffectId;
  private ObjectReference reservationReference;
  private ObjectReference fxReference;
  private final StructuredContent requestedContent;
  private ObjectReference executionAttemptReference;
  private MonetaryExecutionStatus status;
  private List<ObjectReference> movementReferences;
  private List<ObjectReference> resultEvidenceReferences = List.of();

  private TransferMonetaryExecution(
      UUID transferId,
      String fundsService,
      UUID remoteActionId,
      UUID movementGroupId,
      UUID debitEffectId,
      UUID creditEffectId,
      ObjectReference reservationReference,
      ObjectReference fxReference,
      StructuredContent requestedContent,
      MonetaryExecutionStatus status,
      List<ObjectReference> movementReferences,
      ObjectReference executionAttemptReference,
      List<ObjectReference> resultEvidenceReferences) {
    if (transferId == null) {
      throw new DomainInvariantException("transferId es obligatorio.");
    }
    if (fundsService == null || fundsService.isBlank()) {
      throw new DomainInvariantException("fundsService no puede ser nulo ni vacío.");
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
    if (status == MonetaryExecutionStatus.CONFIRMED && (movementReferences == null || movementReferences.isEmpty()))
      throw new DomainInvariantException("CONFIRMED requiere referencias de movimientos.");
    if (movementGroupId != null && (debitEffectId == null || creditEffectId == null))
      throw new DomainInvariantException("Un grupo indivisible requiere identidades de débito y crédito.");
    if (status != MonetaryExecutionStatus.NOT_STARTED
        && movementGroupId == null && debitEffectId == null && creditEffectId == null)
      throw new DomainInvariantException("Una ejecución iniciada requiere identidad estable de efecto.");
    if (status == MonetaryExecutionStatus.UNCERTAIN && executionAttemptReference == null)
      throw new DomainInvariantException("UNCERTAIN requiere referencia del intento original.");
    if (status == MonetaryExecutionStatus.REJECTED && (resultEvidenceReferences == null || resultEvidenceReferences.isEmpty()))
      throw new DomainInvariantException("REJECTED requiere evidencia verificable de ausencia de efecto.");
    this.transferId = transferId;
    this.fundsService = fundsService;
    this.remoteActionId = remoteActionId;
    this.movementGroupId = movementGroupId;
    this.debitEffectId = debitEffectId;
    this.creditEffectId = creditEffectId;
    this.reservationReference = reservationReference;
    this.fxReference = fxReference;
    this.requestedContent = requestedContent;
    this.status = status;
    this.movementReferences = immutableList(movementReferences);
    this.executionAttemptReference = executionAttemptReference;
    this.resultEvidenceReferences = immutableList(resultEvidenceReferences);
  }

  public static TransferMonetaryExecution create(
      UUID transferId,
      String fundsService,
      UUID remoteActionId,
      UUID movementGroupId,
      UUID debitEffectId,
      UUID creditEffectId,
      ObjectReference reservationReference,
      ObjectReference fxReference,
      StructuredContent requestedContent,
      MonetaryExecutionStatus status,
      List<ObjectReference> movementReferences,
      ObjectReference executionAttemptReference) {
    return create(transferId, fundsService, remoteActionId, movementGroupId, debitEffectId, creditEffectId,
        reservationReference, fxReference, requestedContent, status, movementReferences,
        executionAttemptReference, List.of());
  }

  public static TransferMonetaryExecution create(
      UUID transferId, String fundsService, UUID remoteActionId, UUID movementGroupId,
      UUID debitEffectId, UUID creditEffectId, ObjectReference reservationReference,
      ObjectReference fxReference, StructuredContent requestedContent, MonetaryExecutionStatus status,
      List<ObjectReference> movementReferences, ObjectReference executionAttemptReference,
      List<ObjectReference> resultEvidenceReferences) {
    return new TransferMonetaryExecution(transferId, fundsService, remoteActionId, movementGroupId,
        debitEffectId, creditEffectId, reservationReference, fxReference, requestedContent, status,
        movementReferences, executionAttemptReference, resultEvidenceReferences);
  }

  public static TransferMonetaryExecution restore(
      UUID transferId,
      String fundsService,
      UUID remoteActionId,
      UUID movementGroupId,
      UUID debitEffectId,
      UUID creditEffectId,
      ObjectReference reservationReference,
      ObjectReference fxReference,
      StructuredContent requestedContent,
      MonetaryExecutionStatus status,
      List<ObjectReference> movementReferences,
      ObjectReference executionAttemptReference) {
    return create(transferId, fundsService, remoteActionId, movementGroupId, debitEffectId, creditEffectId,
        reservationReference, fxReference, requestedContent, status, movementReferences,
        executionAttemptReference);
  }

  public static TransferMonetaryExecution restore(
      UUID transferId, String fundsService, UUID remoteActionId, UUID movementGroupId,
      UUID debitEffectId, UUID creditEffectId, ObjectReference reservationReference,
      ObjectReference fxReference, StructuredContent requestedContent, MonetaryExecutionStatus status,
      List<ObjectReference> movementReferences, ObjectReference executionAttemptReference,
      List<ObjectReference> resultEvidenceReferences) {
    return create(transferId, fundsService, remoteActionId, movementGroupId, debitEffectId, creditEffectId,
        reservationReference, fxReference, requestedContent, status, movementReferences,
        executionAttemptReference, resultEvidenceReferences);
  }

  public void confirmWithEvidence(List<ObjectReference> movementReferences) {
    if (movementReferences == null || movementReferences.isEmpty()) {
      throw new DomainInvariantException("movementReferences debe contener evidencia disponible.");
    }
    if (this.movementGroupId != null && (this.debitEffectId == null || this.creditEffectId == null))
      throw new DomainInvariantException("Un grupo interno requiere identidades de débito y crédito.");
    if (status == MonetaryExecutionStatus.REJECTED)
      throw new DomainInvariantException("Una ejecución rechazada no puede confirmarse después.");
    List<ObjectReference> confirmedReferences = immutableList(movementReferences);
    if (status == MonetaryExecutionStatus.CONFIRMED) {
      if (this.movementReferences.equals(confirmedReferences)) return;
      throw new DomainInvariantException("La evidencia de una ejecución confirmada no puede alterarse.");
    }
    this.status = MonetaryExecutionStatus.CONFIRMED;
    this.movementReferences = confirmedReferences;
  }

  public void registerRequest(UUID movementGroupId, UUID debitEffectId, UUID creditEffectId,
      ObjectReference reservationReference, ObjectReference fxReference) {
    if (this.status != MonetaryExecutionStatus.NOT_STARTED)
      throw new DomainInvariantException("La intención monetaria ya fue registrada.");
    if (debitEffectId == null && creditEffectId == null)
      throw new DomainInvariantException("Debe identificarse al menos un efecto monetario.");
    if (movementGroupId != null && (debitEffectId == null || creditEffectId == null))
      throw new DomainInvariantException("Un grupo interno requiere débito y crédito identificados.");
    if (this.movementGroupId != null && !this.movementGroupId.equals(movementGroupId))
      throw new DomainInvariantException("movementGroupId no puede cambiar.");
    if (this.debitEffectId != null && !this.debitEffectId.equals(debitEffectId))
      throw new DomainInvariantException("debitEffectId no puede cambiar.");
    if (this.creditEffectId != null && !this.creditEffectId.equals(creditEffectId))
      throw new DomainInvariantException("creditEffectId no puede cambiar.");
    this.movementGroupId = movementGroupId;
    this.debitEffectId = debitEffectId;
    this.creditEffectId = creditEffectId;
    if (reservationReference != null) this.reservationReference = reservationReference;
    if (fxReference != null) this.fxReference = fxReference;
    this.status = MonetaryExecutionStatus.PENDING;
  }

  public void markUncertainWithAttempt(ObjectReference attemptReference) {
    if (attemptReference != null && executionAttemptReference != null
        && !executionAttemptReference.equals(attemptReference))
      throw new DomainInvariantException("La recuperación debe usar la referencia original del intento.");
    if (attemptReference == null && executionAttemptReference == null)
      throw new DomainInvariantException("La incertidumbre requiere conservar el intento original.");
    if (executionAttemptReference == null) executionAttemptReference = attemptReference;
    markUncertain();
  }

  public void registerRequest() {
    if (status != MonetaryExecutionStatus.NOT_STARTED)
      throw new DomainInvariantException("La solicitud monetaria ya fue registrada.");
    if (movementGroupId == null && debitEffectId == null && creditEffectId == null)
      throw new DomainInvariantException("La solicitud debe tener identidad de efecto.");
    status = MonetaryExecutionStatus.PENDING;
  }

  public void rejectWithEvidence(List<ObjectReference> evidenceReferences) {
    if (this.status == MonetaryExecutionStatus.CONFIRMED)
      throw new DomainInvariantException("Una ejecución confirmada no puede rechazarse.");
    if (this.executionAttemptReference == null)
      throw new DomainInvariantException("El rechazo definitivo requiere evidencia del intento original.");
    if (this.status != MonetaryExecutionStatus.PENDING && this.status != MonetaryExecutionStatus.UNCERTAIN
        && this.status != MonetaryExecutionStatus.REJECTED)
      throw new DomainInvariantException("Solo una ejecución pendiente o incierta puede rechazarse.");
    if (evidenceReferences == null || evidenceReferences.isEmpty())
      throw new DomainInvariantException("El rechazo definitivo requiere evidencia de Fondos.");
    List<ObjectReference> evidence = immutableList(evidenceReferences);
    if (this.status == MonetaryExecutionStatus.REJECTED) {
      if (this.resultEvidenceReferences.equals(evidence)) return;
      throw new DomainInvariantException("La evidencia del rechazo definitivo no puede alterarse.");
    }
    this.resultEvidenceReferences = evidence;
    this.status = MonetaryExecutionStatus.REJECTED;
  }

  public void markUncertain() {
    if (this.status == MonetaryExecutionStatus.CONFIRMED || this.status == MonetaryExecutionStatus.REJECTED)
      throw new DomainInvariantException("Un resultado definitivo no puede volver a incierto.");
    if (executionAttemptReference == null)
      throw new DomainInvariantException("UNCERTAIN requiere referencia del intento original.");
    if (status != MonetaryExecutionStatus.PENDING && status != MonetaryExecutionStatus.UNCERTAIN)
      throw new DomainInvariantException("Solo una ejecución pendiente puede pasar a incierta.");
    this.status = MonetaryExecutionStatus.UNCERTAIN;
  }

  private static List<ObjectReference> immutableList(List<ObjectReference> source) {
    if (source == null || source.isEmpty()) {
      return Collections.emptyList();
    }
    ArrayList<ObjectReference> copy = new ArrayList<>();
    for (ObjectReference item : source) {
      if (item == null) {
        throw new DomainInvariantException("movementReferences no puede contener elementos nulos.");
      }
      copy.add(item);
    }
    return Collections.unmodifiableList(copy);
  }

  public UUID getTransferId() { return transferId; }
  public String getFundsService() { return fundsService; }
  public UUID getRemoteActionId() { return remoteActionId; }
  public UUID getMovementGroupId() { return movementGroupId; }
  public UUID getDebitEffectId() { return debitEffectId; }
  public UUID getCreditEffectId() { return creditEffectId; }
  public ObjectReference getReservationReference() { return reservationReference; }
  public ObjectReference getFxReference() { return fxReference; }
  public StructuredContent getRequestedContent() { return requestedContent; }
  public MonetaryExecutionStatus getStatus() { return status; }
  public List<ObjectReference> getMovementReferences() { return movementReferences; }
  public List<ObjectReference> getResultEvidenceReferences() { return resultEvidenceReferences; }
  public ObjectReference getExecutionAttemptReference() { return executionAttemptReference; }
}
