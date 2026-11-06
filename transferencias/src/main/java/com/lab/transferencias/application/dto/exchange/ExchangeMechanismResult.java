package com.lab.transferencias.application.dto.exchange;

import java.time.Instant;
import java.util.List;

/**
 * Resultado de validación de un mecanismo externo con autoridad responsable y evidencias.
 */
public record ExchangeMechanismResult(
    String participantCode,
    String mechanismCode,
    String version,
    boolean enabled,
    String status,
    List<String> evidenceReferences,
    Instant checkedAt,
    String message
) {
}

