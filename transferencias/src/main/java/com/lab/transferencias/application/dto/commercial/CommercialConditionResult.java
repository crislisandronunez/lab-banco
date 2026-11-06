package com.lab.transferencias.application.dto.commercial;

import java.math.BigDecimal;
import java.util.List;
import java.time.Instant;
import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.valueobject.MonetaryConcept;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Resultado con condiciones comerciales y cargos derivados de la operación.
 */
public record CommercialConditionResult(
    boolean applicable,
    String productCode,
    String pricingVersion,
    Currency currency,
    BigDecimal feesAmount,
    List<MonetaryConcept> concepts,
    List<String> appliedRules,
    ObjectReference pricingReference,
    Instant validFrom,
    Instant validUntil,
    List<ObjectReference> evidenceReferences,
    String message
) {

  public CommercialConditionResult {
    concepts = List.copyOf(concepts);
    appliedRules = List.copyOf(appliedRules);
    evidenceReferences = List.copyOf(evidenceReferences);
    if (applicable && (currency == null || feesAmount == null || pricingReference == null || validFrom == null))
      throw new IllegalArgumentException("Una condición aplicable requiere importe, moneda, vigencia y referencia.");
  }
}
