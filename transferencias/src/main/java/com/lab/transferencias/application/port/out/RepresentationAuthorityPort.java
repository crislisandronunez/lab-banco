package com.lab.transferencias.application.port.out;

import com.lab.transferencias.application.dto.representation.RepresentationCheckRequest;
import com.lab.transferencias.application.dto.representation.RepresentationCheckResult;

/**
 * Contrato para verificar la representación del actor y el alcance de la facultad concedida.
 * El resultado conserva la evidencia y la autoridad que valida la representación.
 */
public interface RepresentationAuthorityPort {

  RepresentationCheckResult validateRepresentation(RepresentationCheckRequest request);
}
