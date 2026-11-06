package com.lab.transferencias.application.dto.representation;

import java.util.UUID;
import com.lab.transferencias.domain.valueobject.ObjectReference;

/**
 * Solicitud para comprobar facultades de representación sobre un actor o cuenta.
 */
public record RepresentationCheckRequest(
    UUID actorId,
    UUID subjectId,
    UUID accountId,
    String actionType,
    String scope,
    UUID transferId,
    UUID actionId,
    String contentHash,
    ObjectReference orderingParty
) {
}
