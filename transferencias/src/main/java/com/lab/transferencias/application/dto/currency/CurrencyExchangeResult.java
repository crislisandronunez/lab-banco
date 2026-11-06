package com.lab.transferencias.application.dto.currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Resultado de una conversión de moneda autorizada para la transferencia.
 */
public record CurrencyExchangeResult(
    Currency sourceCurrency,
    Currency targetCurrency,
    BigDecimal sourceAmount,
    BigDecimal convertedAmount,
    BigDecimal rate,
    Instant rateDate,
    long rateVersion,
    ObjectReference conversionReference,
    List<ObjectReference> evidenceReferences
) {

  public CurrencyExchangeResult {
    if (sourceCurrency == null || targetCurrency == null || sourceCurrency == targetCurrency)
      throw new IllegalArgumentException("La conversión requiere monedas distintas.");
    if (sourceAmount == null || sourceAmount.signum() <= 0 || convertedAmount == null || convertedAmount.signum() <= 0)
      throw new IllegalArgumentException("Los importes convertidos deben ser positivos.");
    if (rate == null || rate.signum() <= 0 || rateVersion <= 0 || rateDate == null || conversionReference == null)
      throw new IllegalArgumentException("Tipo, versión, fecha y referencia de cambio son obligatorios.");
    evidenceReferences = List.copyOf(evidenceReferences);
    if (evidenceReferences.isEmpty()) throw new IllegalArgumentException("La conversión requiere evidencia.");
  }
}
