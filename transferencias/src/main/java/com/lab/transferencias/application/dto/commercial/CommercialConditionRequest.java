package com.lab.transferencias.application.dto.commercial;

import java.util.UUID;
import java.math.BigDecimal;
import com.lab.transferencias.domain.enums.Currency;

/**
 * Solicitud de condiciones comerciales y cargos aplicables a la intención de transferencia.
 */
public record CommercialConditionRequest(
    UUID transferId,
    UUID productId,
    String channel,
    String countryCode,
    Currency currency,
    String operationType,
    BigDecimal principalAmount,
    String purposeCode,
    UUID actionId
) {

  public CommercialConditionRequest {
    if (transferId == null || productId == null || actionId == null)
      throw new IllegalArgumentException("transferId, productId y actionId son obligatorios.");
    if (currency == null || principalAmount == null || principalAmount.signum() <= 0)
      throw new IllegalArgumentException("currency e importe positivo son obligatorios.");
    if (operationType == null || operationType.isBlank() || purposeCode == null || purposeCode.isBlank())
      throw new IllegalArgumentException("operationType y purposeCode son obligatorios.");
  }
}
