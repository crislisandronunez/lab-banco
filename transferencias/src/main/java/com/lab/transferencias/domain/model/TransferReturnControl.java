package com.lab.transferencias.domain.model;

import com.lab.transferencias.domain.exception.DomainInvariantException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Controla el remanente disponible para devoluciones y evita que se consuma dos veces el mismo principal.
 */
public class TransferReturnControl {

  private final UUID transferId;
  private BigDecimal originalPrincipal;
  private BigDecimal returnedAmount;
  private BigDecimal pendingAmount;
  private BigDecimal remainingAmount;
  private long lockVersion;
  private Instant updatedAt;

  private TransferReturnControl(
      UUID transferId,
      BigDecimal originalPrincipal,
      BigDecimal returnedAmount,
      BigDecimal pendingAmount,
      BigDecimal remainingAmount,
      long lockVersion,
      Instant updatedAt) {
    if (transferId == null) {
      throw new DomainInvariantException("transferId es obligatorio.");
    }
    if (originalPrincipal == null || originalPrincipal.signum() <= 0) {
      throw new DomainInvariantException("originalPrincipal debe ser mayor que cero.");
    }
    validateScale(originalPrincipal, "originalPrincipal");
    if (returnedAmount == null || returnedAmount.signum() < 0) {
      throw new DomainInvariantException("returnedAmount no puede ser negativo.");
    }
    validateScale(returnedAmount, "returnedAmount");
    if (pendingAmount == null || pendingAmount.signum() < 0) {
      throw new DomainInvariantException("pendingAmount no puede ser negativo.");
    }
    validateScale(pendingAmount, "pendingAmount");
    if (remainingAmount == null || remainingAmount.signum() < 0) {
      throw new DomainInvariantException("remainingAmount no puede ser negativo.");
    }
    validateScale(remainingAmount, "remainingAmount");
    if (originalPrincipal.compareTo(returnedAmount.add(pendingAmount).add(remainingAmount)) != 0) {
      throw new DomainInvariantException("La suma de importes devueltos, pendientes y remanente debe igualar el principal original.");
    }
    this.transferId = transferId;
    this.originalPrincipal = originalPrincipal;
    this.returnedAmount = returnedAmount;
    this.pendingAmount = pendingAmount;
    this.remainingAmount = remainingAmount;
    if (lockVersion < 0) throw new DomainInvariantException("lockVersion no puede ser negativa.");
    this.lockVersion = lockVersion;
    this.updatedAt = updatedAt == null ? Instant.now() : updatedAt;
  }

  public static TransferReturnControl create(
      UUID transferId,
      BigDecimal originalPrincipal,
      BigDecimal returnedAmount,
      BigDecimal pendingAmount,
      BigDecimal remainingAmount,
      long lockVersion,
      Instant updatedAt) {
    return new TransferReturnControl(transferId, originalPrincipal, returnedAmount, pendingAmount,
        remainingAmount, lockVersion, updatedAt);
  }

  public static TransferReturnControl restore(
      UUID transferId,
      BigDecimal originalPrincipal,
      BigDecimal returnedAmount,
      BigDecimal pendingAmount,
      BigDecimal remainingAmount,
      long lockVersion,
      Instant updatedAt) {
    return create(transferId, originalPrincipal, returnedAmount, pendingAmount, remainingAmount,
        lockVersion, updatedAt);
  }

  public BigDecimal availableForReturn() {
    return remainingAmount;
  }

  public void commitReturnAmount(BigDecimal amount) {
    validateAmount(amount);
    if (amount.compareTo(remainingAmount) > 0) {
      throw new DomainInvariantException("El importe comprometido supera el remanente disponible.");
    }
    this.remainingAmount = this.remainingAmount.subtract(amount);
    this.pendingAmount = this.pendingAmount.add(amount);
    this.updatedAt = Instant.now();
    this.lockVersion++;
  }

  public void confirmReturnAmount(BigDecimal amount) {
    validateAmount(amount);
    if (amount.compareTo(pendingAmount) > 0) {
      throw new DomainInvariantException("No puede confirmarse un importe que no está previamente comprometido.");
    }
    this.pendingAmount = this.pendingAmount.subtract(amount);
    this.returnedAmount = this.returnedAmount.add(amount);
    this.updatedAt = Instant.now();
    this.lockVersion++;
  }

  public void releaseRejectedReturn(BigDecimal amount) {
    validateAmount(amount);
    if (amount.compareTo(pendingAmount) > 0) {
      throw new DomainInvariantException("No puede liberarse un importe no comprometido.");
    }
    this.pendingAmount = this.pendingAmount.subtract(amount);
    this.remainingAmount = this.remainingAmount.add(amount);
    this.updatedAt = Instant.now();
    this.lockVersion++;
  }

  private static void validateAmount(BigDecimal amount) {
    if (amount == null || amount.signum() <= 0) {
      throw new DomainInvariantException("amount debe ser mayor que cero.");
    }
    if (amount.scale() > 2) {
      throw new DomainInvariantException("amount no puede tener más de dos decimales.");
    }
  }

  private static void validateScale(BigDecimal amount, String name) {
    if (amount.scale() > 2) throw new DomainInvariantException(name + " no puede tener más de dos decimales.");
  }

  public UUID getTransferId() { return transferId; }
  public BigDecimal getOriginalPrincipal() { return originalPrincipal; }
  public BigDecimal getReturnedAmount() { return returnedAmount; }
  public BigDecimal getPendingAmount() { return pendingAmount; }
  public BigDecimal getRemainingAmount() { return remainingAmount; }
  public long getLockVersion() { return lockVersion; }
  public Instant getUpdatedAt() { return updatedAt; }
}
