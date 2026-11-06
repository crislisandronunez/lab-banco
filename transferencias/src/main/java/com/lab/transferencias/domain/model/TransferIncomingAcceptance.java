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
  private CheckEvidence destinationCheck;
  private CheckEvidence currencyCheck;
  private CheckEvidence controlEvidence;
  private final ObjectReference messageReference;
  private IncomingAcceptanceStatus acceptanceStatus;
  private Instant irrevocablyAcceptedAt;
  private UUID creditEffectId;

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
    if (acceptanceStatus == IncomingAcceptanceStatus.ACCEPTED && irrevocablyAcceptedAt == null)
      throw new DomainInvariantException("ACCEPTED requiere irrevocablyAcceptedAt.");
    if (acceptanceStatus != IncomingAcceptanceStatus.ACCEPTED && irrevocablyAcceptedAt != null)
      throw new DomainInvariantException("Solo ACCEPTED puede tener irrevocablyAcceptedAt.");
    if (acceptanceStatus == IncomingAcceptanceStatus.ACCEPTED && creditEffectId == null)
      throw new DomainInvariantException("ACCEPTED requiere creditEffectId estable.");
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
    if (acceptanceStatus == IncomingAcceptanceStatus.ACCEPTED)
      throw new DomainInvariantException("No se pueden cambiar comprobaciones tras la aceptación firme.");
    this.destinationCheck = destinationCheck;
    this.currencyCheck = currencyCheck;
    this.controlEvidence = controlEvidence;
  }

  public void acceptIrrevocably(Instant acceptedAt) {
    if (acceptanceStatus == IncomingAcceptanceStatus.REJECTED)
      throw new DomainInvariantException("Una aceptación rechazada no puede hacerse irrevocable.");
    if (destinationCheck == null || currencyCheck == null || controlEvidence == null || creditEffectId == null)
      throw new DomainInvariantException("La aceptación firme requiere comprobaciones y efecto de crédito identificado.");
    if (acceptedAt == null) {
      throw new DomainInvariantException("acceptedAt es obligatorio.");
    }
    if (acceptanceStatus == IncomingAcceptanceStatus.ACCEPTED) {
      if (!acceptedAt.equals(irrevocablyAcceptedAt))
        throw new DomainInvariantException("La fecha de aceptación irrevocable no puede cambiar.");
      return;
    }
    this.acceptanceStatus = IncomingAcceptanceStatus.ACCEPTED;
    this.irrevocablyAcceptedAt = acceptedAt;
  }

  public void rejectBeforeAcceptance() {
    if (acceptanceStatus == IncomingAcceptanceStatus.ACCEPTED)
      throw new DomainInvariantException("Una aceptación irrevocable no puede convertirse en rechazo.");
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
    this.creditEffectId = creditEffectId;
  }

  public boolean sameRequestContent(TransferIncomingAcceptance other) {
    return other != null && transferId.equals(other.transferId)
        && destinationCheck.equals(other.destinationCheck) && currencyCheck.equals(other.currencyCheck)
        && controlEvidence.equals(other.controlEvidence) && messageReference.equals(other.messageReference)
        && acceptanceStatus == other.acceptanceStatus
        && Objects.equals(irrevocablyAcceptedAt, other.irrevocablyAcceptedAt)
        && Objects.equals(creditEffectId, other.creditEffectId);
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
