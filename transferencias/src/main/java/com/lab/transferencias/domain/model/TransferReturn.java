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
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa una devolución vinculada a una transferencia y conserva su identidad económica.
 */
public class TransferReturn {

  private final UUID returnId;
  private final UUID transferId;
  private final UUID returnEffectId;
  private BigDecimal amount;
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
    if (status == ReturnStatus.CONFIRMED || status == ReturnStatus.REJECTED) {
      throw new DomainInvariantException("La devolución ya tiene un resultado definido.");
    }
    this.status = ReturnStatus.PENDING;
  }

  public void confirmWithEvidence(List<ObjectReference> resultReferences) {
    if (resultReferences == null || resultReferences.isEmpty()) {
      throw new DomainInvariantException("resultReferences debe contener evidencia suficiente.");
    }
    this.resultReferences = immutableReferences(resultReferences);
    this.status = ReturnStatus.CONFIRMED;
  }

  public void rejectWithEvidence(List<ObjectReference> resultReferences) {
    if (resultReferences == null || resultReferences.isEmpty()) {
      throw new DomainInvariantException("resultReferences debe contener evidencia suficiente.");
    }
    this.resultReferences = immutableReferences(resultReferences);
    this.status = ReturnStatus.REJECTED;
  }

  public void recordUncertainty() {
    this.status = ReturnStatus.UNCERTAIN;
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
