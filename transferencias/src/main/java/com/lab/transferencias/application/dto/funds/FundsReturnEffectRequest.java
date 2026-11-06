package com.lab.transferencias.application.dto.funds;

import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.valueobject.MonetaryConcept;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Solicitud para ejecutar un efecto de devolución asociado a una transferencia original.
 */
public record FundsReturnEffectRequest(
    UUID remoteActionId,
    UUID effectId,
    UUID sourceAccountId,
    UUID destinationAccountId,
    BigDecimal principalAmount,
    Currency currency,
    List<MonetaryConcept> concepts,
    ObjectReference authorizationReference,
    String returnReason
) {

  public FundsReturnEffectRequest {
    if (remoteActionId == null || effectId == null || sourceAccountId == null || destinationAccountId == null)
      throw new IllegalArgumentException("Las identidades y cuentas del retorno son obligatorias.");
    if (principalAmount == null || principalAmount.signum() <= 0 || currency == null)
      throw new IllegalArgumentException("principalAmount positivo y currency son obligatorios.");
    concepts = List.copyOf(concepts);
    if (authorizationReference == null)
      throw new IllegalArgumentException("authorizationReference no puede ser nula.");
    if (returnReason == null || returnReason.isBlank())
      throw new IllegalArgumentException("returnReason no puede estar vacío.");
  }
}

