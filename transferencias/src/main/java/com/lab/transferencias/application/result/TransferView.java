package com.lab.transferencias.application.result;

import com.lab.transferencias.domain.valueobject.AcceptedDestination;
import com.lab.transferencias.domain.valueobject.MonetaryConcept;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import com.lab.transferencias.domain.valueobject.ConfirmedFact;
import com.lab.transferencias.domain.valueobject.PendingComponent;
import com.lab.transferencias.domain.valueobject.AuthorizationBasis;
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
    AuthorizationBasis authorizationBasis,
    List<MonetaryConcept> concepts,
    List<ObjectReference> monetaryEffectReferences,
    List<ConfirmedFact> confirmedFacts,
    List<PendingComponent> pendingComponents,
    List<TransferCancellationView> cancellations,
    List<TransferReturnView> returns,
    long version,
    Instant createdAt,
    Instant receivedAt,
    Instant updatedAt
) {

  public TransferView {
    concepts = List.copyOf(concepts);
    monetaryEffectReferences = List.copyOf(monetaryEffectReferences);
    confirmedFacts = List.copyOf(confirmedFacts);
    pendingComponents = List.copyOf(pendingComponents);
    cancellations = List.copyOf(cancellations);
    returns = List.copyOf(returns);
    if (version <= 0) throw new IllegalArgumentException("version debe ser positiva.");
  }
}

