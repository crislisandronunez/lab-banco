package com.lab.transferencias.application.dto.funds;

import com.lab.transferencias.domain.valueobject.MonetaryConcept;
import com.lab.transferencias.domain.enums.Currency;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Solicitud de reserva de fondos para proteger la cobertura de una transferencia antes de su ejecución.
 */
public record FundsReservationRequest(
    UUID remoteActionId,
    UUID effectId,
    UUID accountId,
    BigDecimal amount,
    Currency currency,
    List<MonetaryConcept> concepts,
    ObjectReference authorizationReference,
    ObjectReference reservationReference
) {

  public FundsReservationRequest {
    concepts = List.copyOf(concepts);
  }
}

