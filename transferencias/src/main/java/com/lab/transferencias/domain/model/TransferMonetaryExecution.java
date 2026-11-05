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
  private final UUID movementGroupId;
  private final UUID debitEffectId;
  private final UUID creditEffectId;
  private final ObjectReference reservationReference;
  private final ObjectReference fxReference;
  private final StructuredContent requestedContent;
  private MonetaryExecutionStatus status;
  private List<ObjectReference> movementReferences;
  private final ObjectReference executionAttemptReference;

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
      ObjectReference executionAttemptReference) {
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
    return new TransferMonetaryExecution(transferId, fundsService, remoteActionId, movementGroupId,
        debitEffectId, creditEffectId, reservationReference, fxReference, requestedContent, status,
        movementReferences, executionAttemptReference);
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

  public void confirmWithEvidence(List<ObjectReference> movementReferences) {
    if (movementReferences == null || movementReferences.isEmpty()) {
      throw new DomainInvariantException("movementReferences debe contener evidencia disponible.");
    }
    this.status = MonetaryExecutionStatus.CONFIRMED;
    this.movementReferences = immutableList(movementReferences);
  }

  public void rejectWithEvidence() {
    this.status = MonetaryExecutionStatus.REJECTED;
  }

  public void markUncertain() {
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
  public ObjectReference getExecutionAttemptReference() { return executionAttemptReference; }
}
