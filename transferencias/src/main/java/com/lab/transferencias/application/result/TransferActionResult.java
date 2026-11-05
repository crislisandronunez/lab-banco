package com.lab.transferencias.application.result;

import java.time.Instant;
import java.util.UUID;

/**
 * Resultado de una actuación ejecutada sobre una transferencia.
 * Resume la identidad del resultado y el seguimiento necesario para consultarlo después.
 */
public record TransferActionResult(
    UUID transferId,
    UUID actionId,
    UUID operationId,
    String status,
    Instant completedAt,
    String message
) {
}

