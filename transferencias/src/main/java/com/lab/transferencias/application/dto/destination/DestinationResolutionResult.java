package com.lab.transferencias.application.dto.destination;

/**
 * Resultado de resolución de un destino externo o interno para la operación.
 */
public record DestinationResolutionResult(
    boolean resolved,
    String destinationKey,
    String beneficiaryName,
    String status,
    String reason
) {
}
