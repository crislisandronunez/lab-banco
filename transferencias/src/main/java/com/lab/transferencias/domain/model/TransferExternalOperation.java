package com.lab.transferencias.domain.model;

import com.lab.transferencias.domain.exception.DomainInvariantException;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import java.util.Objects;
import java.util.UUID;

/**
 * Conserva la identidad de una operación externa y su contexto de intercambio.
 */
public class TransferExternalOperation {

  private final UUID transferId;
  private final UUID participantId;
  private final String mechanismCode;
  private final long mechanismVersion;
  private final String externalOperationId;
  private final String externalContentHash;
  private final ObjectReference exchangeReference;
  private final long schemaVersion;

  private TransferExternalOperation(
      UUID transferId,
      UUID participantId,
      String mechanismCode,
      long mechanismVersion,
      String externalOperationId,
      String externalContentHash,
      ObjectReference exchangeReference,
      long schemaVersion) {
    if (transferId == null) {
      throw new DomainInvariantException("transferId es obligatorio.");
    }
    if (participantId == null) {
      throw new DomainInvariantException("participantId es obligatorio.");
    }
    if (mechanismCode == null || mechanismCode.isBlank()) {
      throw new DomainInvariantException("mechanismCode no puede ser nulo ni vacío.");
    }
    if (mechanismVersion <= 0) {
      throw new DomainInvariantException("mechanismVersion debe ser positivo.");
    }
    if (externalOperationId == null || externalOperationId.isBlank()) {
      throw new DomainInvariantException("externalOperationId no puede ser nulo ni vacío.");
    }
    if (externalContentHash == null || !externalContentHash.matches("^[a-fA-F0-9]{64}$")) {
      throw new DomainInvariantException("externalContentHash debe ser un SHA-256 hexadecimal de 64 caracteres.");
    }
    if (schemaVersion <= 0) {
      throw new DomainInvariantException("schemaVersion debe ser positivo.");
    }
    this.transferId = transferId;
    this.participantId = participantId;
    this.mechanismCode = mechanismCode;
    this.mechanismVersion = mechanismVersion;
    this.externalOperationId = externalOperationId;
    this.externalContentHash = externalContentHash;
    this.exchangeReference = exchangeReference;
    this.schemaVersion = schemaVersion;
  }

  public static TransferExternalOperation create(
      UUID transferId,
      UUID participantId,
      String mechanismCode,
      long mechanismVersion,
      String externalOperationId,
      String externalContentHash,
      ObjectReference exchangeReference,
      long schemaVersion) {
    return new TransferExternalOperation(transferId, participantId, mechanismCode, mechanismVersion,
        externalOperationId, externalContentHash, exchangeReference, schemaVersion);
  }

  public static TransferExternalOperation restore(
      UUID transferId,
      UUID participantId,
      String mechanismCode,
      long mechanismVersion,
      String externalOperationId,
      String externalContentHash,
      ObjectReference exchangeReference,
      long schemaVersion) {
    return new TransferExternalOperation(transferId, participantId, mechanismCode, mechanismVersion,
        externalOperationId, externalContentHash, exchangeReference, schemaVersion);
  }

  public boolean matchesExternalIdentity(UUID participantId, String mechanismCode, String externalOperationId) {
    return Objects.equals(this.participantId, participantId)
        && Objects.equals(this.mechanismCode, mechanismCode)
        && Objects.equals(this.externalOperationId, externalOperationId);
  }

  public boolean verifyContentConsistency(String expectedHash) {
    return Objects.equals(this.externalContentHash, expectedHash);
  }

  public void attachExchangeReference(ObjectReference exchangeReference) {
    if (exchangeReference == null) {
      throw new DomainInvariantException("exchangeReference no puede ser nula.");
    }
    // mantiene el valor inmutable del agregado; la referencia se conserva en la creación
  }

  public UUID getTransferId() { return transferId; }
  public UUID getParticipantId() { return participantId; }
  public String getMechanismCode() { return mechanismCode; }
  public long getMechanismVersion() { return mechanismVersion; }
  public String getExternalOperationId() { return externalOperationId; }
  public String getExternalContentHash() { return externalContentHash; }
  public ObjectReference getExchangeReference() { return exchangeReference; }
  public long getSchemaVersion() { return schemaVersion; }
}
