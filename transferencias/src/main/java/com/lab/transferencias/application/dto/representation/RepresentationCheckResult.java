package com.lab.transferencias.application.dto.representation;

import java.time.Instant;

/**
 * Resultado de la validación de representación en la operación solicitada.
 */
public record RepresentationCheckResult(
    boolean authorized,
    String authority,
    String scope,
    Instant evaluatedAt,
    String justification
) {
}
