package com.lab.transferencias.application.dto.funds;

import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.valueobject.MonetaryConcept;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Solicitud de movimientos internos relacionados con una actuación, una reserva y una identidad económica estable.
 */
public record FundsExecutionRequest(
    UUID actionId,
    UUID remoteActionId,
    UUID effectId,
    UUID sourceAccountId,
    UUID destinationAccountId,
    BigDecimal principalAmount,
    Currency currency,
    List<MonetaryConcept> concepts,
    String authorizationReference,
    String reservationReference
) {
}
