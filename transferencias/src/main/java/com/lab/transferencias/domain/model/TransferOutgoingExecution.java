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
  private ObjectReference exchangeReference;
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
    if ((beneficiaryCreditedAt == null) != (creditEvidenceReference == null))
      throw new DomainInvariantException("La fecha y evidencia de acreditación deben informarse juntas.");
    if (beneficiaryCreditedAt != null && irrevocablyAcceptedAt == null)
      throw new DomainInvariantException("No puede haber acreditación sin aceptación irrevocable.");
    if (dispatchAuthorizedAt != null && exchangeReference == null)
      throw new DomainInvariantException("El despacho autorizado requiere intercambio identificado.");
    if (lockVersion < 0) throw new DomainInvariantException("lockVersion no puede ser negativa.");
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
    this.lockVersion = lockVersion;
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
    if (beneficiaryCreditedAt != null && status != OutgoingCoverageStatus.RESOLVED)
      throw new DomainInvariantException("La cobertura acreditada solo puede quedar resuelta.");
    if (coverageStatus == OutgoingCoverageStatus.RESOLVED && status != OutgoingCoverageStatus.RESOLVED)
      throw new DomainInvariantException("La cobertura resuelta no puede reabrirse.");
    if (dispatchAuthorizedAt != null && status == OutgoingCoverageStatus.PREPARING)
      throw new DomainInvariantException("No se puede liberar cobertura después de autorizar el despacho.");
    this.coverageStatus = status;
    this.updatedAt = Instant.now();
  }

  public void protectCoverage() {
    if (this.coverageStatus != OutgoingCoverageStatus.PROTECTED)
      throw new DomainInvariantException("La cobertura debe estar protegida y sin resolución pendiente antes del despacho.");
  }

  public void authorizeDispatch(Instant dispatchedAt) {
    if (dispatchedAt == null) {
      throw new DomainInvariantException("dispatchAuthorizedAt es obligatorio.");
    }
    protectCoverage();
    if (this.dispatchAuthorizedAt != null)
      throw new DomainInvariantException("La intención de despacho ya está registrada.");
    this.dispatchAuthorizedAt = dispatchedAt;
    this.updatedAt = Instant.now();
  }

  public void authorizeDispatch(Instant dispatchedAt, ObjectReference exchangeReference) {
    if (exchangeReference == null) throw new DomainInvariantException("El intercambio debe quedar identificado antes del despacho.");
    if (this.exchangeReference != null && !this.exchangeReference.equals(exchangeReference))
      throw new DomainInvariantException("La identidad del intercambio no puede cambiar.");
    this.exchangeReference = exchangeReference;
    authorizeDispatch(dispatchedAt);
  }

  public void recordIrrevocableAcceptance(Instant acceptedAt) {
    if (acceptedAt == null) {
      throw new DomainInvariantException("irrevocablyAcceptedAt es obligatorio.");
    }
    if (dispatchAuthorizedAt == null)
      throw new DomainInvariantException("No puede aceptarse irrevocablemente sin despacho autorizado.");
    if (irrevocablyAcceptedAt != null)
      throw new DomainInvariantException("La aceptación irrevocable ya fue registrada.");
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
    if (irrevocablyAcceptedAt == null)
      throw new DomainInvariantException("No puede acreditarse antes de la aceptación irrevocable.");
    if (beneficiaryCreditedAt != null
        && (!beneficiaryCreditedAt.equals(creditedAt) || !creditEvidenceReference.equals(evidenceReference)))
      throw new DomainInvariantException("La acreditación confirmada no puede alterarse.");
    this.beneficiaryCreditedAt = creditedAt;
    this.creditEvidenceReference = evidenceReference;
    this.updatedAt = Instant.now();
  }

  public void recordDefinitiveRejection(ObjectReference evidence, boolean futureExecutionPending) {
    if (evidence == null) throw new DomainInvariantException("El rechazo definitivo requiere evidencia.");
    if (futureExecutionPending) throw new DomainInvariantException("No se puede rechazar mientras exista ejecución futura posible.");
    if (irrevocablyAcceptedAt != null || beneficiaryCreditedAt != null)
      throw new DomainInvariantException("Una aceptación irrevocable no puede convertirse en rechazo definitivo.");
    if (coverageStatus == OutgoingCoverageStatus.RESOLVED)
      throw new DomainInvariantException("La cobertura ya fue resuelta.");
    coverageStatus = OutgoingCoverageStatus.RESOLVED;
    pendingComponents = Collections.emptyList();
    updatedAt = Instant.now();
    lockVersion++;
  }

  public void linkReservationConsumption(UUID consumptionStepId) {
    if (consumptionStepId == null) {
      throw new DomainInvariantException("consumptionStepId es obligatorio.");
    }
    if (beneficiaryCreditedAt == null)
      throw new DomainInvariantException("La reserva solo se consume tras acreditar al beneficiario.");
    if (!this.consumptionStepId.equals(consumptionStepId))
      throw new DomainInvariantException("La identidad del consumo de reserva no puede cambiar.");
  }

  public void resolveCoverage(OutgoingCoverageStatus status, List<PendingComponent> remaining) {
    if (status != OutgoingCoverageStatus.RESOLVED && status != OutgoingCoverageStatus.RESOLUTION_PENDING
        && status != OutgoingCoverageStatus.PROTECTED && status != OutgoingCoverageStatus.PREPARING)
      throw new DomainInvariantException("Estado de cobertura inválido.");
    if (this.beneficiaryCreditedAt != null && status == OutgoingCoverageStatus.PREPARING)
      throw new DomainInvariantException("No se puede reiniciar cobertura tras acreditar al beneficiario.");
    this.coverageStatus = status;
    this.pendingComponents = immutableComponents(remaining);
    this.updatedAt = Instant.now();
    this.lockVersion++;
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
