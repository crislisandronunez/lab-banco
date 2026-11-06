package com.lab.transferencias.application.dto.persistence;

import java.time.Instant;
import java.util.UUID;

/**
 * Trabajo de recuperación para reintentar un paso remoto o un resultado pendiente.
 */
public record RecoveryJobRecord(
    UUID jobId,
    UUID transferId,
    UUID remoteStepId,
    String jobType,
    String status,
    Instant nextRunAt,
    Instant createdAt
) {
}
