package com.lab.transferencias.domain.model;

import com.lab.transferencias.domain.enums.IncomingAcceptanceStatus;
import com.lab.transferencias.domain.exception.DomainInvariantException;
import com.lab.transferencias.domain.valueobject.CheckEvidence;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Conserva la aceptación o rechazo de una transferencia recibida desde un participante externo.
 */
public class TransferIncomingAcceptance {

  private final UUID transferId;
  private final CheckEvidence destinationCheck;
  private final CheckEvidence currencyCheck;
  private final CheckEvidence controlEvidence;
  private final ObjectReference messageReference;
  private IncomingAcceptanceStatus acceptanceStatus;
  private Instant irrevocablyAcceptedAt;
  private final UUID creditEffectId;

  private TransferIncomingAcceptance(
      UUID transferId,
      CheckEvidence destinationCheck,
      CheckEvidence currencyCheck,
      CheckEvidence controlEvidence,
      ObjectReference messageReference,
      IncomingAcceptanceStatus acceptanceStatus,
      Instant irrevocablyAcceptedAt,
      UUID creditEffectId) {
    if (transferId == null) {
      throw new DomainInvariantException("transferId es obligatorio.");
    }
    if (destinationCheck == null) {
      throw new DomainInvariantException("destinationCheck es obligatorio.");
    }
    if (currencyCheck == null) {
      throw new DomainInvariantException("currencyCheck es obligatorio.");
    }
    if (controlEvidence == null) {
      throw new DomainInvariantException("controlEvidence es obligatorio.");
    }
    if (messageReference == null) {
      throw new DomainInvariantException("messageReference es obligatorio.");
    }
    if (acceptanceStatus == null) {
      throw new DomainInvariantException("acceptanceStatus es obligatorio.");
    }
    this.transferId = transferId;
    this.destinationCheck = destinationCheck;
    this.currencyCheck = currencyCheck;
    this.controlEvidence = controlEvidence;
    this.messageReference = messageReference;
    this.acceptanceStatus = acceptanceStatus;
    this.irrevocablyAcceptedAt = irrevocablyAcceptedAt;
    this.creditEffectId = creditEffectId;
  }

  public static TransferIncomingAcceptance create(
      UUID transferId,
      CheckEvidence destinationCheck,
      CheckEvidence currencyCheck,
      CheckEvidence controlEvidence,
      ObjectReference messageReference,
      IncomingAcceptanceStatus acceptanceStatus,
      Instant irrevocablyAcceptedAt,
      UUID creditEffectId) {
    return new TransferIncomingAcceptance(transferId, destinationCheck, currencyCheck, controlEvidence,
        messageReference, acceptanceStatus, irrevocablyAcceptedAt, creditEffectId);
  }

  public static TransferIncomingAcceptance restore(
      UUID transferId,
      CheckEvidence destinationCheck,
      CheckEvidence currencyCheck,
      CheckEvidence controlEvidence,
      ObjectReference messageReference,
      IncomingAcceptanceStatus acceptanceStatus,
      Instant irrevocablyAcceptedAt,
      UUID creditEffectId) {
    return new TransferIncomingAcceptance(transferId, destinationCheck, currencyCheck, controlEvidence,
        messageReference, acceptanceStatus, irrevocablyAcceptedAt, creditEffectId);
  }

  public void recordChecks(CheckEvidence destinationCheck, CheckEvidence currencyCheck, CheckEvidence controlEvidence) {
    if (destinationCheck == null || currencyCheck == null || controlEvidence == null) {
      throw new DomainInvariantException("Las comprobaciones no pueden ser nulas.");
    }
    // Se conservan como datos de la aceptación; la entidad es inmutable en sus referencias esenciales.
  }

  public void acceptIrrevocably(Instant acceptedAt) {
    if (acceptedAt == null) {
      throw new DomainInvariantException("acceptedAt es obligatorio.");
    }
    this.acceptanceStatus = IncomingAcceptanceStatus.ACCEPTED;
    this.irrevocablyAcceptedAt = acceptedAt;
  }

  public void rejectBeforeAcceptance() {
    this.acceptanceStatus = IncomingAcceptanceStatus.REJECTED;
    this.irrevocablyAcceptedAt = null;
  }

  public void associateUniqueCreditEffect(UUID creditEffectId) {
    if (creditEffectId == null) {
      throw new DomainInvariantException("creditEffectId es obligatorio.");
    }
    if (this.creditEffectId != null && !this.creditEffectId.equals(creditEffectId)) {
      throw new DomainInvariantException("creditEffectId ya está asociado y no puede cambiarse.");
    }
  }

  public UUID getTransferId() { return transferId; }
  public CheckEvidence getDestinationCheck() { return destinationCheck; }
  public CheckEvidence getCurrencyCheck() { return currencyCheck; }
  public CheckEvidence getControlEvidence() { return controlEvidence; }
  public ObjectReference getMessageReference() { return messageReference; }
  public IncomingAcceptanceStatus getAcceptanceStatus() { return acceptanceStatus; }
  public Instant getIrrevocablyAcceptedAt() { return irrevocablyAcceptedAt; }
  public UUID getCreditEffectId() { return creditEffectId; }
}
