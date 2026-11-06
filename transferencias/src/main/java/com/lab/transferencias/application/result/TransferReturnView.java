package com.lab.transferencias.application.result;

import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.enums.ReturnStatus;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** Datos de consulta de una devolución vinculada. */
public record TransferReturnView(
    UUID returnId,
    UUID returnEffectId,
    BigDecimal amount,
    Currency currency,
    String reasonCode,
    ReturnStatus status,
    List<ObjectReference> resultReferences
) {

  public TransferReturnView {
    resultReferences = List.copyOf(resultReferences);
  }
}
