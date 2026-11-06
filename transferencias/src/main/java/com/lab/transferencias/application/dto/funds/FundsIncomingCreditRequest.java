package com.lab.transferencias.application.dto.funds;

import com.lab.transferencias.domain.enums.Currency;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Solicitud de acreditación de un movimiento de entrada con su identidad y evidencia original.
 */
public record FundsIncomingCreditRequest(
    UUID remoteActionId,
    UUID effectId,
    UUID destinationAccountId,
    BigDecimal principalAmount,
    Currency currency,
    List<String> concepts,
    String authorizationReference,
    String externalReference
) {
}

