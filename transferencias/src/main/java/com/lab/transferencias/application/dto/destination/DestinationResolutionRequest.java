package com.lab.transferencias.application.dto.destination;

import java.util.UUID;

/**
 * Solicitud para resolver un destino bancario aceptado por la institución.
 */
public record DestinationResolutionRequest(
    String bankIdentifierType,
    String bankIdentifierValue,
    String countryCode,
    UUID requestingPartyId
) {
}
