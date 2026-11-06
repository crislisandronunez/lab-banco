package com.lab.transferencias.domain.model;

import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.enums.ReturnStatus;
import com.lab.transferencias.domain.exception.DomainInvariantException;
import com.lab.transferencias.domain.valueobject.AuthorizationBasis;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import com.lab.transferencias.domain.valueobject.ReturnDestination;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.List;

/**
 * Representa una devolución vinculada a una transferencia y conserva su identidad económica.
 */
public class TransferReturn {

  private final UUID returnId;
  private final UUID transferId;
  private final UUID returnEffectId;
  private final BigDecimal amount;
  private final Currency currency;
  private final String reasonCode;
  private final ReturnDestination returnDestination;
  private final AuthorizationBasis authorizationBasis;
  private ReturnStatus status;
  private List<ObjectReference> resultReferences;
  private final UUID createdActionId;
  private final Instant createdAt;
  private long version;
  private long lockVersion;
  private List<TransferReturnStep> steps = List.of();

  private TransferReturn(
      UUID returnId,
      UUID transferId,
      UUID returnEffectId,
      BigDecimal amount,
      Currency currency,
      String reasonCode,
      ReturnDestination returnDestination,
      AuthorizationBasis authorizationBasis,
      ReturnStatus status,
      List<ObjectReference> resultReferences,
      UUID createdActionId,
      Instant createdAt,
      long version,
      long lockVersion) {
    if (returnId == null) {
      throw new DomainInvariantException("returnId es obligatorio.");
    }
    if (transferId == null) {
      throw new DomainInvariantException("transferId es obligatorio.");
    }
    if (returnEffectId == null) {
      throw new DomainInvariantException("returnEffectId es obligatorio.");
    }
    if (amount == null || amount.signum() <= 0) {
      throw new DomainInvariantException("amount debe ser mayor que cero.");
    }
    if (amount.scale() > 2) throw new DomainInvariantException("amount no puede tener más de dos decimales.");
    if (currency == null) {
      throw new DomainInvariantException("currency es obligatorio.");
    }
    if (reasonCode == null || reasonCode.isBlank() || reasonCode.length() > 80) {
      throw new DomainInvariantException("reasonCode debe tener hasta 80 caracteres.");
    }
    if (returnDestination == null) {
      throw new DomainInvariantException("returnDestination es obligatorio.");
    }
    if (authorizationBasis == null) {
      throw new DomainInvariantException("authorizationBasis es obligatorio.");
    }
    if (status == null) {
      throw new DomainInvariantException("status es obligatorio.");
    }
    if ((status == ReturnStatus.CONFIRMED || status == ReturnStatus.REJECTED)
        && (resultReferences == null || resultReferences.isEmpty()))
      throw new DomainInvariantException("Un resultado definitivo requiere evidencia.");
    if (createdActionId == null) {
      throw new DomainInvariantException("createdActionId es obligatorio.");
    }
    if (createdAt == null) {
      throw new DomainInvariantException("createdAt es obligatorio.");
    }
    if (version <= 0) {
      throw new DomainInvariantException("version debe ser positiva.");
    }
    if (lockVersion < 0) {
      throw new DomainInvariantException("lockVersion no puede ser negativa.");
    }
    this.returnId = returnId;
    this.transferId = transferId;
    this.returnEffectId = returnEffectId;
    this.amount = amount;
    this.currency = currency;
    this.reasonCode = reasonCode;
    this.returnDestination = returnDestination;
    this.authorizationBasis = authorizationBasis;
    this.status = status;
    this.resultReferences = immutableReferences(resultReferences);
    this.createdActionId = createdActionId;
    this.createdAt = createdAt;
    this.version = version;
    this.lockVersion = lockVersion;
  }

  public static TransferReturn create(
      UUID returnId,
      UUID transferId,
      UUID returnEffectId,
      BigDecimal amount,
      Currency currency,
      String reasonCode,
      ReturnDestination returnDestination,
      AuthorizationBasis authorizationBasis,
      ReturnStatus status,
      List<ObjectReference> resultReferences,
      UUID createdActionId,
      Instant createdAt,
      long version,
      long lockVersion) {
    return new TransferReturn(returnId, transferId, returnEffectId, amount, currency, reasonCode,
        returnDestination, authorizationBasis, status, resultReferences, createdActionId, createdAt,
        version, lockVersion);
  }

  public static TransferReturn restore(
      UUID returnId,
      UUID transferId,
      UUID returnEffectId,
      BigDecimal amount,
      Currency currency,
      String reasonCode,
      ReturnDestination returnDestination,
      AuthorizationBasis authorizationBasis,
      ReturnStatus status,
      List<ObjectReference> resultReferences,
      UUID createdActionId,
      Instant createdAt,
      long version,
      long lockVersion) {
    return create(returnId, transferId, returnEffectId, amount, currency, reasonCode, returnDestination,
        authorizationBasis, status, resultReferences, createdActionId, createdAt, version, lockVersion);
  }

  public void registerReturn() {
    if (status != ReturnStatus.PENDING)
      throw new DomainInvariantException("Solo se puede registrar una devolución pendiente.");
    this.status = ReturnStatus.PENDING;
  }

  public void validateDestinationCurrency() {
    if (returnDestination.currency() != currency)
      throw new DomainInvariantException("El destino y la devolución deben tener la misma moneda.");
  }

  public void confirmWithEvidence(List<ObjectReference> resultReferences) {
    if (status == ReturnStatus.REJECTED) throw new DomainInvariantException("Una devolución rechazada no puede confirmarse.");
    if (resultReferences == null || resultReferences.isEmpty()) {
      throw new DomainInvariantException("resultReferences debe contener evidencia suficiente.");
    }
    List<ObjectReference> evidence = immutableReferences(resultReferences);
    if (status == ReturnStatus.CONFIRMED) {
      if (this.resultReferences.equals(evidence)) return;
      throw new DomainInvariantException("La evidencia de una devolución confirmada no puede alterarse.");
    }
    this.resultReferences = evidence;
    this.status = ReturnStatus.CONFIRMED;
  }

  public void rejectWithEvidence(List<ObjectReference> resultReferences) {
    if (status == ReturnStatus.CONFIRMED) throw new DomainInvariantException("Una devolución confirmada no puede rechazarse sin revertir antes el control.");
    if (resultReferences == null || resultReferences.isEmpty()) {
      throw new DomainInvariantException("resultReferences debe contener evidencia suficiente.");
    }
    List<ObjectReference> evidence = immutableReferences(resultReferences);
    if (status == ReturnStatus.REJECTED) {
      if (this.resultReferences.equals(evidence)) return;
      throw new DomainInvariantException("La evidencia de un rechazo confirmado no puede alterarse.");
    }
    this.resultReferences = evidence;
    this.status = ReturnStatus.REJECTED;
  }

  public void recordUncertainty() {
    throw new DomainInvariantException("La incertidumbre debe conservar evidencia del intento.");
  }

  public void requireReview(List<ObjectReference> evidence) {
    if (status == ReturnStatus.CONFIRMED || status == ReturnStatus.REJECTED)
      throw new DomainInvariantException("Un resultado definitivo no puede pasar a revisión.");
    if (evidence == null || evidence.isEmpty())
      throw new DomainInvariantException("La revisión requiere evidencia contradictoria o insuficiente.");
    this.resultReferences = immutableReferences(evidence);
    this.status = ReturnStatus.REQUIRES_REVIEW;
  }

  public void recordUncertainty(List<ObjectReference> evidence) {
    if (evidence == null || evidence.isEmpty())
      throw new DomainInvariantException("La incertidumbre debe conservar evidencia del intento.");
    if (status == ReturnStatus.CONFIRMED || status == ReturnStatus.REJECTED)
      throw new DomainInvariantException("Un resultado definitivo no puede volver a incierto.");
    resultReferences = immutableReferences(evidence);
    status = ReturnStatus.UNCERTAIN;
  }

  public void attachStep(TransferReturnStep step) {
    Objects.requireNonNull(step, "step");
    if (!returnId.equals(step.getReturnId())) throw new DomainInvariantException("El paso remoto pertenece a otra devolución.");
    if (steps.stream().anyMatch(s -> s.getComponentCode().equals(step.getComponentCode())))
      throw new DomainInvariantException("Ya existe un paso para ese componente de devolución.");
    if (steps.stream().anyMatch(s -> s.hasSameRemoteIdentity(step.getRemoteActionId(), step.getResponsibleService())))
      throw new DomainInvariantException("La identidad remota ya está asociada a esta devolución.");
    ArrayList<TransferReturnStep> copy = new ArrayList<>(steps); copy.add(step);
    steps = Collections.unmodifiableList(copy);
  }

  public List<TransferReturnStep> getSteps() { return steps; }

  public boolean sameRequestContent(TransferReturn other) {
    return other != null && returnId.equals(other.returnId) && transferId.equals(other.transferId)
        && returnEffectId.equals(other.returnEffectId) && amount.compareTo(other.amount) == 0
        && currency == other.currency && reasonCode.equals(other.reasonCode)
        && returnDestination.equals(other.returnDestination)
        && authorizationBasis.equals(other.authorizationBasis);
  }

  private static List<ObjectReference> immutableReferences(List<ObjectReference> source) {
    if (source == null || source.isEmpty()) {
      return Collections.emptyList();
    }
    ArrayList<ObjectReference> copy = new ArrayList<>();
    for (ObjectReference item : source) {
      if (item == null) {
        throw new DomainInvariantException("resultReferences no puede contener elementos nulos.");
      }
      copy.add(item);
    }
    return Collections.unmodifiableList(copy);
  }

  public UUID getReturnId() { return returnId; }
  public UUID getTransferId() { return transferId; }
  public UUID getReturnEffectId() { return returnEffectId; }
  public BigDecimal getAmount() { return amount; }
  public Currency getCurrency() { return currency; }
  public String getReasonCode() { return reasonCode; }
  public ReturnDestination getReturnDestination() { return returnDestination; }
  public AuthorizationBasis getAuthorizationBasis() { return authorizationBasis; }
  public ReturnStatus getStatus() { return status; }
  public List<ObjectReference> getResultReferences() { return resultReferences; }
  public UUID getCreatedActionId() { return createdActionId; }
  public Instant getCreatedAt() { return createdAt; }
  public long getVersion() { return version; }
  public long getLockVersion() { return lockVersion; }
}
