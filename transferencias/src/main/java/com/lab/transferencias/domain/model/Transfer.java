package com.lab.transferencias.domain.model;

import com.lab.transferencias.domain.enums.CancellationOutcome;
import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.enums.ProcessingState;
import com.lab.transferencias.domain.enums.TransferType;
import com.lab.transferencias.domain.exception.DomainInvariantException;
import com.lab.transferencias.domain.valueobject.AcceptedDestination;
import com.lab.transferencias.domain.valueobject.AuthorizationBasis;
import com.lab.transferencias.domain.valueobject.ConfirmedFact;
import com.lab.transferencias.domain.valueobject.MonetaryConcept;
import com.lab.transferencias.domain.valueobject.ObjectReference;
import com.lab.transferencias.domain.valueobject.PendingComponent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Raíz del agregado que conserva la instrucción de transferencia y controla sus invariantes.
 */
public class Transfer {

  private final UUID transferId;
  private TransferType transferType;
  private String originService;
  private String originType;
  private String originReference;
  private ObjectReference orderingPartyReference;
  private Instant receivedAt;
  private UUID sourceAccountId;
  private UUID destinationAccountId;
  private AcceptedDestination acceptedDestination;
  private BigDecimal principalAmount;
  private Currency currency;
  private String purposeCode;
  private AuthorizationBasis authorizationBasis;
  private String acceptedContentHash;
  private List<MonetaryConcept> concepts;
  private ProcessingState processingState;
  private boolean dispatchBarrier;
  private List<PendingComponent> pendingComponents;
  private List<ConfirmedFact> confirmedFacts;
  private List<ObjectReference> monetaryEffectReferences;
  private long version;
  private long lockVersion;
  private UUID createdActionId;
  private Instant createdAt;
  private Instant updatedAt;

  private Transfer(
      UUID transferId,
      TransferType transferType,
      String originService,
      String originType,
      String originReference,
      ObjectReference orderingPartyReference,
      Instant receivedAt,
      UUID sourceAccountId,
      UUID destinationAccountId,
      AcceptedDestination acceptedDestination,
      BigDecimal principalAmount,
      Currency currency,
      String purposeCode,
      AuthorizationBasis authorizationBasis,
      String acceptedContentHash,
      List<MonetaryConcept> concepts,
      ProcessingState processingState,
      boolean dispatchBarrier,
      List<PendingComponent> pendingComponents,
      List<ConfirmedFact> confirmedFacts,
      List<ObjectReference> monetaryEffectReferences,
      long version,
      long lockVersion,
      UUID createdActionId,
      Instant createdAt,
      Instant updatedAt) {
    validateRequiredFields(transferId, transferType, originService, originType, originReference,
        orderingPartyReference, receivedAt, principalAmount, currency, purposeCode, acceptedContentHash,
        createdActionId, createdAt, updatedAt);
    validateTypeSpecificRules(transferType, sourceAccountId, destinationAccountId, acceptedDestination);
    validateAmount(principalAmount);
    validateStringLength("purposeCode", purposeCode, 80);
    validateStringLength("originReference", originReference, 120);
    validateContentHash(acceptedContentHash);
    this.transferId = transferId;
    this.transferType = transferType;
    this.originService = originService;
    this.originType = originType;
    this.originReference = originReference;
    this.orderingPartyReference = orderingPartyReference;
    this.receivedAt = receivedAt;
    this.sourceAccountId = sourceAccountId;
    this.destinationAccountId = destinationAccountId;
    this.acceptedDestination = acceptedDestination;
    this.principalAmount = principalAmount;
    this.currency = currency;
    this.purposeCode = purposeCode;
    this.authorizationBasis = authorizationBasis;
    this.acceptedContentHash = acceptedContentHash;
    this.concepts = immutableConcepts(concepts);
    this.processingState = processingState == null ? ProcessingState.RECEIVED : processingState;
    this.dispatchBarrier = dispatchBarrier;
    this.pendingComponents = immutablePendingComponents(pendingComponents);
    this.confirmedFacts = immutableConfirmedFacts(confirmedFacts);
    this.monetaryEffectReferences = immutableObjectReferences(monetaryEffectReferences);
    this.version = version > 0 ? version : 1L;
    this.lockVersion = lockVersion >= 0 ? lockVersion : 0L;
    this.createdActionId = createdActionId;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static Transfer create(
      UUID transferId,
      TransferType transferType,
      String originService,
      String originType,
      String originReference,
      ObjectReference orderingPartyReference,
      Instant receivedAt,
      UUID sourceAccountId,
      UUID destinationAccountId,
      AcceptedDestination acceptedDestination,
      BigDecimal principalAmount,
      Currency currency,
      String purposeCode,
      AuthorizationBasis authorizationBasis,
      String acceptedContentHash,
      List<MonetaryConcept> concepts,
      UUID createdActionId,
      Instant createdAt) {
    return new Transfer(transferId, transferType, originService, originType, originReference,
        orderingPartyReference, receivedAt, sourceAccountId, destinationAccountId, acceptedDestination,
        principalAmount, currency, purposeCode, authorizationBasis, acceptedContentHash,
        concepts, ProcessingState.RECEIVED, false, List.of(), List.of(), List.of(), 1L, 0L,
        createdActionId, createdAt, createdAt);
  }

  public static Transfer restore(
      UUID transferId,
      TransferType transferType,
      String originService,
      String originType,
      String originReference,
      ObjectReference orderingPartyReference,
      Instant receivedAt,
      UUID sourceAccountId,
      UUID destinationAccountId,
      AcceptedDestination acceptedDestination,
      BigDecimal principalAmount,
      Currency currency,
      String purposeCode,
      AuthorizationBasis authorizationBasis,
      String acceptedContentHash,
      List<MonetaryConcept> concepts,
      ProcessingState processingState,
      boolean dispatchBarrier,
      List<PendingComponent> pendingComponents,
      List<ConfirmedFact> confirmedFacts,
      List<ObjectReference> monetaryEffectReferences,
      long version,
      long lockVersion,
      UUID createdActionId,
      Instant createdAt,
      Instant updatedAt) {
    return new Transfer(transferId, transferType, originService, originType, originReference,
        orderingPartyReference, receivedAt, sourceAccountId, destinationAccountId, acceptedDestination,
        principalAmount, currency, purposeCode, authorizationBasis, acceptedContentHash,
        concepts, processingState, dispatchBarrier, pendingComponents, confirmedFacts,
        monetaryEffectReferences, version, lockVersion, createdActionId, createdAt, updatedAt);
  }

  public void acceptInstruction(AcceptedDestination destination) {
    Objects.requireNonNull(destination, "destination");
    if (destination.destinationType() == null) {
      throw new DomainInvariantException("El destino aceptado no es válido.");
    }
    this.acceptedDestination = destination;
    this.updatedAt = Instant.now();
    if (this.processingState == ProcessingState.RECEIVED) {
      this.processingState = ProcessingState.VALIDATING;
    }
  }

  public void fixAcceptedDestination(AcceptedDestination destination) {
    acceptInstruction(destination);
  }

  public void recordAuthorization(AuthorizationBasis basis) {
    this.authorizationBasis = Objects.requireNonNull(basis, "basis");
    this.updatedAt = Instant.now();
  }

  public void recordMonetaryOutcome(ObjectReference reference) {
    if (reference == null) {
      throw new DomainInvariantException("reference no puede ser nula.");
    }
    ArrayList<ObjectReference> updated = new ArrayList<>(this.monetaryEffectReferences);
    updated.add(reference);
    this.monetaryEffectReferences = Collections.unmodifiableList(updated);
    this.updatedAt = Instant.now();
  }

  public void recordConfirmedFact(ConfirmedFact fact) {
    Objects.requireNonNull(fact, "fact");
    if (this.confirmedFacts.stream().anyMatch(existing -> existing.factType() == fact.factType()
        && existing.sourceReference().equals(fact.sourceReference()))) {
      return;
    }
    ArrayList<ConfirmedFact> updated = new ArrayList<>(this.confirmedFacts);
    updated.add(fact);
    this.confirmedFacts = Collections.unmodifiableList(updated);
    this.updatedAt = Instant.now();
  }

  public void recordPendingComponent(PendingComponent component) {
    Objects.requireNonNull(component, "component");
    if (this.pendingComponents.stream().anyMatch(existing -> existing.componentCode().equals(component.componentCode()))) {
      return;
    }
    ArrayList<PendingComponent> updated = new ArrayList<>(this.pendingComponents);
    updated.add(component);
    this.pendingComponents = Collections.unmodifiableList(updated);
    this.updatedAt = Instant.now();
  }

  public void protectDispatch() {
    if (dispatchBarrier) {
      throw new DomainInvariantException("La transferencia está protegida frente al despacho.");
    }
    if (processingState == ProcessingState.CANCELLED) {
      throw new DomainInvariantException("No se puede despachar una transferencia cancelada.");
    }
  }

  public void applyCancellation(TransferCancellation cancellation) {
    Objects.requireNonNull(cancellation, "cancellation");
    if (cancellation.getOutcome() == CancellationOutcome.CANCELLED
        || cancellation.getOutcome() == CancellationOutcome.ALREADY_CREDITED) {
      this.dispatchBarrier = true;
    }
    if (this.processingState != ProcessingState.CANCELLED && cancellation.getOutcome() == CancellationOutcome.CANCELLED) {
      this.processingState = ProcessingState.CANCELLED;
    }
    this.updatedAt = Instant.now();
  }

  public void registerReturn(TransferReturn transferReturn) {
    Objects.requireNonNull(transferReturn, "transferReturn");
    if (this.processingState == ProcessingState.COMPLETED) {
      this.processingState = ProcessingState.REQUIRES_RESOLUTION;
    }
    this.updatedAt = Instant.now();
  }

  private static void validateRequiredFields(
      UUID transferId,
      TransferType transferType,
      String originService,
      String originType,
      String originReference,
      ObjectReference orderingPartyReference,
      Instant receivedAt,
      BigDecimal principalAmount,
      Currency currency,
      String purposeCode,
      String acceptedContentHash,
      UUID createdActionId,
      Instant createdAt,
      Instant updatedAt) {
    if (transferId == null) {
      throw new DomainInvariantException("transferId es obligatorio.");
    }
    if (transferType == null) {
      throw new DomainInvariantException("transferType es obligatorio.");
    }
    if (originService == null || originService.isBlank()) {
      throw new DomainInvariantException("originService no puede ser nulo ni vacío.");
    }
    if (originType == null || originType.isBlank()) {
      throw new DomainInvariantException("originType no puede ser nulo ni vacío.");
    }
    if (originReference == null || originReference.isBlank()) {
      throw new DomainInvariantException("originReference no puede ser nulo ni vacío.");
    }
    if (orderingPartyReference == null) {
      throw new DomainInvariantException("orderingPartyReference es obligatorio.");
    }
    if (receivedAt == null) {
      throw new DomainInvariantException("receivedAt es obligatorio.");
    }
    if (principalAmount == null) {
      throw new DomainInvariantException("principalAmount es obligatorio.");
    }
    if (currency == null) {
      throw new DomainInvariantException("currency es obligatorio.");
    }
    if (purposeCode == null || purposeCode.isBlank()) {
      throw new DomainInvariantException("purposeCode no puede ser nulo ni vacío.");
    }
    if (acceptedContentHash == null || acceptedContentHash.isBlank()) {
      throw new DomainInvariantException("acceptedContentHash es obligatorio.");
    }
    if (createdActionId == null) {
      throw new DomainInvariantException("createdActionId es obligatorio.");
    }
    if (createdAt == null) {
      throw new DomainInvariantException("createdAt es obligatorio.");
    }
    if (updatedAt == null) {
      throw new DomainInvariantException("updatedAt es obligatorio.");
    }
  }

  private static void validateTypeSpecificRules(TransferType transferType, UUID sourceAccountId, UUID destinationAccountId,
      AcceptedDestination acceptedDestination) {
    if (transferType == TransferType.INTERNAL) {
      if (sourceAccountId == null || destinationAccountId == null) {
        throw new DomainInvariantException("Para INTERNAL deben existir originAccountId y destinationAccountId.");
      }
      if (sourceAccountId.equals(destinationAccountId)) {
        throw new DomainInvariantException("Las cuentas de origen y destino no pueden ser iguales.");
      }
    }
    if (transferType == TransferType.OUTGOING && acceptedDestination == null) {
      throw new DomainInvariantException("OUTGOING requiere acceptedDestination.");
    }
    if (transferType == TransferType.INCOMING && destinationAccountId == null) {
      throw new DomainInvariantException("INCOMING requiere destinationAccountId.");
    }
  }

  private static void validateAmount(BigDecimal principalAmount) {
    if (principalAmount.signum() <= 0) {
      throw new DomainInvariantException("principalAmount debe ser mayor que cero.");
    }
    if (principalAmount.scale() > 2) {
      throw new DomainInvariantException("principalAmount no puede tener más de dos decimales.");
    }
  }

  private static void validateStringLength(String fieldName, String value, int maxLength) {
    if (value != null && value.length() > maxLength) {
      throw new DomainInvariantException(fieldName + " excede la longitud máxima permitida de " + maxLength + ".");
    }
  }

  private static void validateContentHash(String hash) {
    if (!hash.matches("^[a-fA-F0-9]{64}$")) {
      throw new DomainInvariantException("acceptedContentHash debe ser un SHA-256 hexadecimal de 64 caracteres.");
    }
  }

    private static List<MonetaryConcept> immutableConcepts(List<MonetaryConcept> source) {
    if (source == null) return Collections.emptyList();
    ArrayList<MonetaryConcept> copy = new ArrayList<>();
    for (MonetaryConcept item : source) {
      if (item == null) throw new DomainInvariantException("concepts no puede contener elementos nulos.");
      copy.add(item);
    }
    return Collections.unmodifiableList(copy);
    }

    private static List<PendingComponent> immutablePendingComponents(List<PendingComponent> source) {
    if (source == null) return Collections.emptyList();
    ArrayList<PendingComponent> copy = new ArrayList<>();
    for (PendingComponent item : source) {
      if (item == null) throw new DomainInvariantException("pendingComponents no puede contener elementos nulos.");
      copy.add(item);
    }
    return Collections.unmodifiableList(copy);
    }

    private static List<ConfirmedFact> immutableConfirmedFacts(List<ConfirmedFact> source) {
    if (source == null) return Collections.emptyList();
    ArrayList<ConfirmedFact> copy = new ArrayList<>();
    for (ConfirmedFact item : source) {
      if (item == null) throw new DomainInvariantException("confirmedFacts no puede contener elementos nulos.");
      copy.add(item);
    }
    return Collections.unmodifiableList(copy);
    }

    private static List<ObjectReference> immutableObjectReferences(List<ObjectReference> source) {
    if (source == null) return Collections.emptyList();
    ArrayList<ObjectReference> copy = new ArrayList<>();
    for (ObjectReference item : source) {
      if (item == null) throw new DomainInvariantException("monetaryEffectReferences no puede contener elementos nulos.");
      copy.add(item);
    }
    return Collections.unmodifiableList(copy);
    }

  public UUID getTransferId() { return transferId; }
  public TransferType getTransferType() { return transferType; }
  public String getOriginService() { return originService; }
  public String getOriginType() { return originType; }
  public String getOriginReference() { return originReference; }
  public ObjectReference getOrderingPartyReference() { return orderingPartyReference; }
  public Instant getReceivedAt() { return receivedAt; }
  public UUID getSourceAccountId() { return sourceAccountId; }
  public UUID getDestinationAccountId() { return destinationAccountId; }
  public AcceptedDestination getAcceptedDestination() { return acceptedDestination; }
  public BigDecimal getPrincipalAmount() { return principalAmount; }
  public Currency getCurrency() { return currency; }
  public String getPurposeCode() { return purposeCode; }
  public AuthorizationBasis getAuthorizationBasis() { return authorizationBasis; }
  public String getAcceptedContentHash() { return acceptedContentHash; }
  public List<MonetaryConcept> getConcepts() { return concepts; }
  public ProcessingState getProcessingState() { return processingState; }
  public boolean isDispatchBarrier() { return dispatchBarrier; }
  public List<PendingComponent> getPendingComponents() { return pendingComponents; }
  public List<ConfirmedFact> getConfirmedFacts() { return confirmedFacts; }
  public List<ObjectReference> getMonetaryEffectReferences() { return monetaryEffectReferences; }
  public long getVersion() { return version; }
  public long getLockVersion() { return lockVersion; }
  public UUID getCreatedActionId() { return createdActionId; }
  public Instant getCreatedAt() { return createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
}
