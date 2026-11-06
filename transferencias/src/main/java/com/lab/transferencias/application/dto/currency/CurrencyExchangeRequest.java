package com.lab.transferencias.application.dto.currency;

import java.math.BigDecimal;
import java.util.UUID;
import com.lab.transferencias.domain.enums.Currency;

/**
 * Solicitud de conversión de moneda para la operación de transferencia.
 */
public record CurrencyExchangeRequest(
    UUID transferId,
    UUID actionId,
    Currency sourceCurrency,
    Currency targetCurrency,
    BigDecimal amount,
    String rateType
) {

  public CurrencyExchangeRequest {
    if (transferId == null || actionId == null)
      throw new IllegalArgumentException("transferId y actionId son obligatorios.");
    if (sourceCurrency == null || targetCurrency == null || sourceCurrency == targetCurrency)
      throw new IllegalArgumentException("La conversión requiere monedas distintas.");
    if (amount == null || amount.signum() <= 0)
      throw new IllegalArgumentException("amount debe ser positivo.");
    if (rateType == null || rateType.isBlank()) throw new IllegalArgumentException("rateType no puede estar vacío.");
  }
}
