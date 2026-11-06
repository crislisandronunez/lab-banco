package com.lab.transferencias.application.port.out;

import com.lab.transferencias.application.dto.commercial.CommercialConditionRequest;
import com.lab.transferencias.application.dto.commercial.CommercialConditionResult;

/**
 * Contrato para obtener condiciones comerciales y conceptos aplicables a la operación.
 * El resultado conserva la determinación comercial y la evidencia de la autoridad responsable.
 */
public interface CommercialConditionsPort {

  CommercialConditionResult resolveCommercialConditions(CommercialConditionRequest request);
}
