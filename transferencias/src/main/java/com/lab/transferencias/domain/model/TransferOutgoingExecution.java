package com.lab.transferencias.domain.model;

import com.lab.transferencias.domain.enums.OutgoingCoverageStatus;
import com.lab.transferencias.domain.exception.DomainInvariantException;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import com.lab.transferencias.domain.valueobject.PendingComponent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Conserva la cobertura, el despacho y la acreditación asociada a una transferencia saliente.
 */
public class TransferOutgoingExecution {

  private final UUID transferId;
  private final ObjectReference mechanismReference;
  private final ObjectReference reservationReference;
  private OutgoingCoverageStatus coverageStatus;
  private final ObjectReference exchangeReference;
  private Instant dispatchAuthorizedAt;
  private Instant irrevocablyAcceptedAt;
  private Instant beneficiaryCreditedAt;
  private ObjectReference creditEvidenceReference;
  private final UUID consumptionStepId;
  private List<PendingComponent> pendingComponents;
  private Instant updatedAt;
  private long lockVersion;

  private TransferOutgoingExecution(
      UUID transferId,
      ObjectReference mechanismReference,
      ObjectReference reservationReference,
      OutgoingCoverageStatus coverageStatus,
      ObjectReference exchangeReference,
      Instant dispatchAuthorizedAt,
      Instant irrevocablyAcceptedAt,
      Instant beneficiaryCreditedAt,
      ObjectReference creditEvidenceReference,
      UUID consumptionStepId,
      List<PendingComponent> pendingComponents,
      Instant updatedAt,
      long lockVersion) {
    if (transferId == null) {
      throw new DomainInvariantException("transferId es obligatorio.");
    }
    if (mechanismReference == null) {
      throw new DomainInvariantException("mechanismReference es obligatorio.");
    }
    if (reservationReference == null) {
      throw new DomainInvariantException("reservationReference es obligatorio.");
    }
    if (coverageStatus == null) {
      throw new DomainInvariantException("coverageStatus es obligatorio.");
    }
    if (consumptionStepId == null) {
      throw new DomainInvariantException("consumptionStepId es obligatorio.");
    }
    this.transferId = transferId;
    this.mechanismReference = mechanismReference;
    this.reservationReference = reservationReference;
    this.coverageStatus = coverageStatus;
    this.exchangeReference = exchangeReference;
    this.dispatchAuthorizedAt = dispatchAuthorizedAt;
    this.irrevocablyAcceptedAt = irrevocablyAcceptedAt;
    this.beneficiaryCreditedAt = beneficiaryCreditedAt;
    this.creditEvidenceReference = creditEvidenceReference;
    this.consumptionStepId = consumptionStepId;
    this.pendingComponents = immutableComponents(pendingComponents);
    this.updatedAt = updatedAt == null ? Instant.now() : updatedAt;
    this.lockVersion = lockVersion >= 0 ? lockVersion : 0L;
  }

  public static TransferOutgoingExecution create(
      UUID transferId,
      ObjectReference mechanismReference,
      ObjectReference reservationReference,
      OutgoingCoverageStatus coverageStatus,
      ObjectReference exchangeReference,
      Instant dispatchAuthorizedAt,
      Instant irrevocablyAcceptedAt,
      Instant beneficiaryCreditedAt,
      ObjectReference creditEvidenceReference,
      UUID consumptionStepId,
      List<PendingComponent> pendingComponents,
      Instant updatedAt,
      long lockVersion) {
    return new TransferOutgoingExecution(transferId, mechanismReference, reservationReference, coverageStatus,
        exchangeReference, dispatchAuthorizedAt, irrevocablyAcceptedAt, beneficiaryCreditedAt,
        creditEvidenceReference, consumptionStepId, pendingComponents, updatedAt, lockVersion);
  }

  public static TransferOutgoingExecution restore(
      UUID transferId,
      ObjectReference mechanismReference,
      ObjectReference reservationReference,
      OutgoingCoverageStatus coverageStatus,
      ObjectReference exchangeReference,
      Instant dispatchAuthorizedAt,
      Instant irrevocablyAcceptedAt,
      Instant beneficiaryCreditedAt,
      ObjectReference creditEvidenceReference,
      UUID consumptionStepId,
      List<PendingComponent> pendingComponents,
      Instant updatedAt,
      long lockVersion) {
    return create(transferId, mechanismReference, reservationReference, coverageStatus, exchangeReference,
        dispatchAuthorizedAt, irrevocablyAcceptedAt, beneficiaryCreditedAt, creditEvidenceReference,
        consumptionStepId, pendingComponents, updatedAt, lockVersion);
  }

  public void registerCoverage(OutgoingCoverageStatus status) {
    if (status == null) {
      throw new DomainInvariantException("status es obligatorio.");
    }
    this.coverageStatus = status;
    this.updatedAt = Instant.now();
  }

  public void protectCoverage() {
    if (this.coverageStatus == OutgoingCoverageStatus.RESOLUTION_PENDING) {
      throw new DomainInvariantException("La cobertura requiere resolución antes de continuar.");
    }
  }

  public void authorizeDispatch(Instant dispatchedAt) {
    if (dispatchedAt == null) {
      throw new DomainInvariantException("dispatchAuthorizedAt es obligatorio.");
    }
    this.dispatchAuthorizedAt = dispatchedAt;
    this.updatedAt = Instant.now();
  }

  public void recordIrrevocableAcceptance(Instant acceptedAt) {
    if (acceptedAt == null) {
      throw new DomainInvariantException("irrevocablyAcceptedAt es obligatorio.");
    }
    this.irrevocablyAcceptedAt = acceptedAt;
    this.updatedAt = Instant.now();
  }

  public void recordBeneficiaryCredit(Instant creditedAt, ObjectReference evidenceReference) {
    if (creditedAt == null) {
      throw new DomainInvariantException("beneficiaryCreditedAt es obligatorio.");
    }
    if (evidenceReference == null) {
      throw new DomainInvariantException("creditEvidenceReference es obligatorio.");
    }
    this.beneficiaryCreditedAt = creditedAt;
    this.creditEvidenceReference = evidenceReference;
    this.updatedAt = Instant.now();
  }

  public void linkReservationConsumption(UUID consumptionStepId) {
    if (consumptionStepId == null) {
      throw new DomainInvariantException("consumptionStepId es obligatorio.");
    }
    // este valor se conserva como identidad estable del paso de consumo.
  }

  private static List<PendingComponent> immutableComponents(List<PendingComponent> source) {
    if (source == null || source.isEmpty()) {
      return Collections.emptyList();
    }
    ArrayList<PendingComponent> copy = new ArrayList<>();
    for (PendingComponent item : source) {
      if (item == null) {
        throw new DomainInvariantException("pendingComponents no puede contener elementos nulos.");
      }
      copy.add(item);
    }
    return Collections.unmodifiableList(copy);
  }

  public UUID getTransferId() { return transferId; }
  public ObjectReference getMechanismReference() { return mechanismReference; }
  public ObjectReference getReservationReference() { return reservationReference; }
  public OutgoingCoverageStatus getCoverageStatus() { return coverageStatus; }
  public ObjectReference getExchangeReference() { return exchangeReference; }
  public Instant getDispatchAuthorizedAt() { return dispatchAuthorizedAt; }
  public Instant getIrrevocablyAcceptedAt() { return irrevocablyAcceptedAt; }
  public Instant getBeneficiaryCreditedAt() { return beneficiaryCreditedAt; }
  public ObjectReference getCreditEvidenceReference() { return creditEvidenceReference; }
  public UUID getConsumptionStepId() { return consumptionStepId; }
  public List<PendingComponent> getPendingComponents() { return pendingComponents; }
  public Instant getUpdatedAt() { return updatedAt; }
  public long getLockVersion() { return lockVersion; }
}
