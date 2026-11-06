package com.lab.transferencias.application.port.out;

import com.lab.transferencias.application.dto.controls.BusinessControlRequest;
import com.lab.transferencias.application.dto.controls.BusinessControlResult;

/**
 * Contrato para evaluar restricciones, límites y controles de riesgo y cumplimiento aplicables.
 * El resultado identifica la decisión, el alcance y la autoridad responsable.
 */
public interface BusinessControlsPort {

  BusinessControlResult evaluateControls(BusinessControlRequest request);
}
