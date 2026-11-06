package com.lab.transferencias.application.dto.controls;

import java.time.Instant;

/**
 * Resultado de un control de negocio aplicado a la operación.
 */
public record BusinessControlResult(
    boolean approved,
    String controlCode,
    String decision,
    String reason,
    Instant evaluatedAt
) {
}
