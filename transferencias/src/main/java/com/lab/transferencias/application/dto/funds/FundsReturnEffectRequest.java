package com.lab.transferencias.application.dto.funds;

import com.lab.transferencias.domain.enums.Currency;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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
    List<String> concepts,
    String authorizationReference,
    String returnReason
) {
}

