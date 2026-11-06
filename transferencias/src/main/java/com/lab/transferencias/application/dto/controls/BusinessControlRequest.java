package com.lab.transferencias.application.dto.controls;

import java.util.UUID;

/**
 * Solicitud de control de riesgo o cumplimiento para una operación de transferencia.
 */
public record BusinessControlRequest(
    UUID transferId,
    UUID actorId,
    String controlType,
    String operationType,
    String countryCode
) {
}
