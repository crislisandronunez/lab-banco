package com.lab.transferencias.application.dto.commercial;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resultado con condiciones comerciales y cargos derivados de la operación.
 */
public record CommercialConditionResult(
    boolean applicable,
    String productCode,
    String pricingVersion,
    BigDecimal feesAmount,
    List<String> appliedRules,
    String message
) {
}
