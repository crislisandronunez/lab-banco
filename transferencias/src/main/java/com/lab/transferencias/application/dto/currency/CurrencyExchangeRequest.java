package com.lab.transferencias.application.dto.currency;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Solicitud de conversión de moneda para la operación de transferencia.
 */
public record CurrencyExchangeRequest(
    UUID transferId,
    UUID actionId,
    String sourceCurrency,
    String targetCurrency,
    BigDecimal amount,
    String rateType
) {
}
