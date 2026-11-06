package com.lab.transferencias.application.dto.representation;

import java.util.UUID;

/**
 * Solicitud para comprobar facultades de representación sobre un actor o cuenta.
 */
public record RepresentationCheckRequest(
    UUID actorId,
    UUID subjectId,
    UUID accountId,
    String actionType,
    String scope
) {
}
