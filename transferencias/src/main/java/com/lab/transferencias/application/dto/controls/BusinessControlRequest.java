package com.lab.transferencias.application.dto.controls;

import java.util.UUID;
import java.math.BigDecimal;
import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.valueobject.AcceptedDestination;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Solicitud de control de riesgo o cumplimiento para una operación de transferencia.
 */
public record BusinessControlRequest(
    UUID transferId,
    UUID actorId,
    String controlType,
    String operationType,
    String countryCode,
    UUID actionId,
    ObjectReference orderingParty,
    AcceptedDestination acceptedDestination,
    BigDecimal principalAmount,
    Currency currency,
    String purposeCode
) {

  public BusinessControlRequest {
    if (transferId == null || actorId == null || actionId == null)
      throw new IllegalArgumentException("transferId, actorId y actionId son obligatorios.");
    if (principalAmount == null || principalAmount.signum() <= 0 || currency == null)
      throw new IllegalArgumentException("principalAmount positivo y currency son obligatorios.");
    if (controlType == null || controlType.isBlank() || operationType == null || operationType.isBlank())
      throw new IllegalArgumentException("controlType y operationType son obligatorios.");
    if (purposeCode == null || purposeCode.isBlank())
      throw new IllegalArgumentException("purposeCode no puede estar vacío.");
  }
}
