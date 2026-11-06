package com.lab.transferencias.application.dto.exchange;

import com.lab.transferencias.domain.enums.Currency;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Solicitud para tramitar una devolución del intercambio externo asociado a la transferencia.
 */
public record ExchangeReturnRequest(
    UUID exchangeId,
    UUID remoteActionId,
    UUID effectId,
    String participantCode,
    String mechanismCode,
    BigDecimal principalAmount,
    Currency currency,
    List<String> concepts,
    String authorizationReference,
    String returnReason
) {
}

