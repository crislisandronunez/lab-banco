package com.lab.transferencias.application.dto.currency;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Resultado de una conversión de moneda autorizada para la transferencia.
 */
public record CurrencyExchangeResult(
    String sourceCurrency,
    String targetCurrency,
    BigDecimal sourceAmount,
    BigDecimal convertedAmount,
    BigDecimal rate,
    Instant rateDate,
    String referenceId
) {
}
