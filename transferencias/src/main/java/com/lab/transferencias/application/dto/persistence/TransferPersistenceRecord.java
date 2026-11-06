package com.lab.transferencias.application.dto.persistence;

import java.time.Instant;
import java.util.UUID;

/**
 * Snapshot persistido de una transferencia para repositorios y consultas locales.
 */
public record TransferPersistenceRecord(
    UUID transferId,
    String transferType,
    String originService,
    String originType,
    String originReference,
    UUID sourceAccountId,
    UUID destinationAccountId,
    String status,
    Instant updatedAt
) {
}
