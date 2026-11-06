package com.lab.transferencias.application.dto.commercial;

import java.util.UUID;

/**
 * Solicitud de condiciones comerciales y cargos aplicables a la intención de transferencia.
 */
public record CommercialConditionRequest(
    UUID transferId,
    UUID productId,
    String channel,
    String countryCode,
    String currencyCode
) {
}
