package com.lab.transferencias.application.port.out;

import com.lab.transferencias.application.dto.destination.DestinationResolutionRequest;
import com.lab.transferencias.application.dto.destination.DestinationResolutionResult;

/**
 * Contrato para resolver un destino bancario y devolver una resolución verificable para la operación.
 * La respuesta conserva la identidad del destino, la autoridad y la evidencia del resultado.
 */
public interface DestinationResolutionPort {

  DestinationResolutionResult resolveDestination(DestinationResolutionRequest request);
}
