package com.lab.transferencias.application.command;

import com.lab.transferencias.application.exception.InvalidActionContextException;
import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.valueobject.AcceptedDestination;
import com.lab.transferencias.domain.valueobject.AuthorizationBasis;
import com.lab.transferencias.domain.valueobject.MonetaryConcept;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Comando de negocio para ejecutar una transferencia interna con sus datos autorizados.
 * Define la instrucción que debe ser validada, registrada y coordinada con la autoridad de fondos.
 */
public record ExecuteInternalTransferCommand(
    ActionContext context,
    ObjectReference orderingParty,
    UUID sourceAccountId,
    AcceptedDestination acceptedDestination,
    BigDecimal principalAmount,
    Currency currency,
    String purposeCode,
    AuthorizationBasis authorizationBasis,
    List<MonetaryConcept> concepts,
    List<ObjectReference> commercialReferences,
    ObjectReference fxReference
) {

  public ExecuteInternalTransferCommand {
    if (context == null) {
      throw new InvalidActionContextException("context no puede ser nulo.");
    }
    if (orderingParty == null) {
      throw new InvalidActionContextException("orderingParty no puede ser nulo.");
    }
    if (sourceAccountId == null) {
      throw new InvalidActionContextException("sourceAccountId no puede ser nulo.");
    }
    if (acceptedDestination == null) {
      throw new InvalidActionContextException("acceptedDestination no puede ser nulo.");
    }
    if (principalAmount == null || principalAmount.signum() <= 0) {
      throw new InvalidActionContextException("principalAmount debe ser positivo.");
    }
    if (currency == null) {
      throw new InvalidActionContextException("currency no puede ser nulo.");
    }
    if (purposeCode == null || purposeCode.isBlank()) {
      throw new InvalidActionContextException("purposeCode no puede ser nulo ni vacío.");
    }
    if (authorizationBasis == null) {
      throw new InvalidActionContextException("authorizationBasis no puede ser nulo.");
    }
    if (concepts == null || concepts.isEmpty()) {
      throw new InvalidActionContextException("concepts no puede ser nulo ni vacío.");
    }
    concepts = List.copyOf(concepts);
    commercialReferences = commercialReferences == null ? List.of() : List.copyOf(commercialReferences);
    if (fxReference != null && fxReference.objectType() == null) {
      throw new InvalidActionContextException("fxReference no es válido para esta operación.");
    }
  }
}

