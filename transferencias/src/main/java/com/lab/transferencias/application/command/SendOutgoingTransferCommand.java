package com.lab.transferencias.application.command;

import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.valueobject.AcceptedDestination;
import com.lab.transferencias.domain.valueobject.AuthorizationBasis;
import com.lab.transferencias.domain.valueobject.MonetaryConcept;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Comando para preparar y enviar una transferencia externa saliente.
 * Recoge la instrucción aceptada, el contenido autorizado y la información necesaria para cubrir y despachar la operación.
 */
public record SendOutgoingTransferCommand(
    ActionContext context,
    ObjectReference orderingParty,
    UUID sourceAccountId,
    AcceptedDestination acceptedDestination,
    ObjectReference mechanismReference,
    BigDecimal principalAmount,
    Currency currency,
    String purposeCode,
    AuthorizationBasis authorizationBasis,
    List<MonetaryConcept> concepts,
    List<ObjectReference> commercialReferences,
    ObjectReference fxReference
) {

  public SendOutgoingTransferCommand {
    if (context == null) {
      throw new IllegalArgumentException("context no puede ser nulo.");
    }
    if (orderingParty == null) {
      throw new IllegalArgumentException("orderingParty no puede ser nulo.");
    }
    if (sourceAccountId == null) {
      throw new IllegalArgumentException("sourceAccountId no puede ser nulo.");
    }
    if (acceptedDestination == null) {
      throw new IllegalArgumentException("acceptedDestination no puede ser nulo.");
    }
    if (mechanismReference == null) {
      throw new IllegalArgumentException("mechanismReference no puede ser nulo.");
    }
    if (principalAmount == null || principalAmount.signum() <= 0) {
      throw new IllegalArgumentException("principalAmount debe ser positivo.");
    }
    if (currency == null) {
      throw new IllegalArgumentException("currency no puede ser nulo.");
    }
    if (purposeCode == null || purposeCode.isBlank()) {
      throw new IllegalArgumentException("purposeCode no puede ser nulo ni vacío.");
    }
    if (authorizationBasis == null) {
      throw new IllegalArgumentException("authorizationBasis no puede ser nulo.");
    }
    if (concepts == null || concepts.isEmpty()) {
      throw new IllegalArgumentException("concepts no puede ser nulo ni vacío.");
    }
    concepts = List.copyOf(concepts);
    commercialReferences = commercialReferences == null ? List.of() : List.copyOf(commercialReferences);
  }
}

