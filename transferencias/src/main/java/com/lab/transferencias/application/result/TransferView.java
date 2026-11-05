package com.lab.transferencias.application.result;

import com.lab.transferencias.domain.valueobject.AcceptedDestination;
import com.lab.transferencias.domain.valueobject.MonetaryConcept;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Vista autorizada de una transferencia para consulta de lectura.
 * Expone la información de negocio relevante sin exponer detalles de infraestructura.
 */
public record TransferView(
    UUID transferId,
    String transferType,
    UUID sourceAccountId,
    UUID destinationAccountId,
    AcceptedDestination acceptedDestination,
    BigDecimal principalAmount,
    String currency,
    String purposeCode,
    String processingState,
    List<MonetaryConcept> concepts,
    List<ObjectReference> monetaryEffectReferences,
    Instant updatedAt
) {
}

