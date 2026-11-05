package com.lab.transferencias.application.result;

import java.time.Instant;
import java.util.UUID;

/**
 * Vista autorizada de una actuación persistente para consulta.
 * Resume la identidad de la operación, el estado y la evidencia vinculada.
 */
public record TransferActionView(
    UUID actionId,
    UUID operationId,
    UUID transferId,
    String actionType,
    String status,
    Instant createdAt,
    Instant completedAt,
    String message
) {
}

