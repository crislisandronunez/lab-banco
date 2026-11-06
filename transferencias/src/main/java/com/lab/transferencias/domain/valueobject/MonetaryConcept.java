package com.lab.transferencias.domain.valueobject;

import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.enums.MonetaryComponentType;
import com.lab.transferencias.domain.exception.DomainInvariantException;
import java.math.BigDecimal;

/**
 * Representa cada componente monetario de una transferencia con su finalidad y importe.
 */
public record MonetaryConcept(
    String conceptCode,
    MonetaryComponentType componentType,
    BigDecimal amount,
    Currency currency,
    ObjectReference calculationReference
) {

  public MonetaryConcept {
    if (conceptCode == null || conceptCode.isBlank() || conceptCode.length() > 80) {
      throw new DomainInvariantException("conceptCode debe tener entre 1 y 80 caracteres.");
    }
    if (componentType == null) {
      throw new DomainInvariantException("componentType es obligatorio.");
    }
    if (amount == null) {
      throw new DomainInvariantException("amount es obligatorio.");
    }
    if (amount.signum() < 0) {
      throw new DomainInvariantException("amount no puede ser negativo.");
    }
    if (amount.scale() > 2) {
      throw new DomainInvariantException("amount no puede tener más de dos decimales.");
    }
    if (componentType == MonetaryComponentType.PRINCIPAL && amount.signum() <= 0)
      throw new DomainInvariantException("El concepto PRINCIPAL debe ser positivo.");
    if (currency == null) {
      throw new DomainInvariantException("currency es obligatorio.");
    }
  }
}
