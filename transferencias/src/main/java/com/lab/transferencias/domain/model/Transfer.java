package com.lab.transferencias.domain.model;

import com.lab.transferencias.domain.enums.CancellationOutcome;
import com.lab.transferencias.domain.enums.Currency;
import com.lab.transferencias.domain.enums.DestinationType;
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
  private long canonicalizationVersion;
  private String authorizedCanonicalContent;
  private List<MonetaryConcept> concepts;
  private ProcessingState processingState;
  private boolean dispatchBarrier;
  private List<PendingComponent> pendingComponents;
  private List<ConfirmedFact> confirmedFacts;
  private List<ObjectReference> rejectionEvidenceReferences = List.of();
  private List<ObjectReference> monetaryEffectReferences;
  private TransferOutgoingExecution outgoingExecution;
  private TransferMonetaryExecution monetaryExecution;
  private List<TransferMonetaryExecution> monetaryExecutions = List.of();
  private TransferIncomingAcceptance incomingAcceptance;
  private TransferExternalOperation externalOperation;
  private List<TransferExternalOperation> externalOperations = List.of();
  private TransferReturnControl returnControl;
  private List<TransferReturn> returns = List.of();
  private List<TransferCancellation> cancellations = List.of();
  private List<TransferRemoteStep> remoteSteps = List.of();
  private List<TransferFact> facts = List.of();
  private long version;
  private long lockVersion;
  private UUID createdActionId;
  private Instant createdAt;
  private Instant updatedAt;

  private Transfer(UUID transferId, TransferType transferType, String originService, String originType, String originReference, ObjectReference orderingPartyReference, Instant receivedAt, UUID sourceAccountId, UUID destinationAccountId, AcceptedDestination acceptedDestination, BigDecimal principalAmount, Currency currency, String purposeCode, AuthorizationBasis authorizationBasis, String acceptedContentHash, List<MonetaryConcept> concepts, ProcessingState processingState, boolean dispatchBarrier, List<PendingComponent> pendingComponents, List<ConfirmedFact> confirmedFacts, List<ObjectReference> monetaryEffectReferences, long version, long lockVersion, UUID createdActionId, Instant createdAt, Instant updatedAt, List<ObjectReference> rejectionEvidenceReferences) {
    validateRequiredFields(transferId, transferType, originService, originType, originReference, orderingPartyReference, receivedAt, principalAmount, currency, purposeCode, acceptedContentHash, createdActionId, createdAt, updatedAt);
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
    this.canonicalizationVersion = 1L;
    this.authorizedCanonicalContent = null;
    if (authorizationBasis != null && !authorizationBasis.contentHash().equalsIgnoreCase(acceptedContentHash))
      throw new DomainInvariantException("La autorización inicial no corresponde al hash de la instrucción.");
    this.concepts = immutableConcepts(concepts);
    validatePrincipalConcept();
    this.processingState = processingState == null ? ProcessingState.RECEIVED : processingState;
    this.dispatchBarrier = dispatchBarrier;
    this.pendingComponents = immutablePendingComponents(pendingComponents);
    this.confirmedFacts = immutableConfirmedFacts(confirmedFacts);
    this.rejectionEvidenceReferences = immutableObjectReferences(rejectionEvidenceReferences);
    if (processingState == ProcessingState.REJECTED && this.rejectionEvidenceReferences.isEmpty())
      throw new DomainInvariantException("REJECTED requiere evidencia definitiva.");
    if (processingState == ProcessingState.CANCELLED && !dispatchBarrier)
      throw new DomainInvariantException("CANCELLED requiere barrera de despacho activa.");
    this.monetaryEffectReferences = immutableObjectReferences(monetaryEffectReferences);
    if (version <= 0) throw new DomainInvariantException("version debe ser positiva.");
    if (lockVersion < 0) throw new DomainInvariantException("lockVersion no puede ser negativa.");
    this.version = version;
    this.lockVersion = lockVersion;
    this.createdActionId = createdActionId;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static Transfer create(UUID transferId, TransferType transferType, String originService, String originType, String originReference, ObjectReference orderingPartyReference, Instant receivedAt, UUID sourceAccountId, UUID destinationAccountId, AcceptedDestination acceptedDestination, BigDecimal principalAmount, Currency currency, String purposeCode, AuthorizationBasis authorizationBasis, String acceptedContentHash, List<MonetaryConcept> concepts, UUID createdActionId, Instant createdAt) {
    return new Transfer(transferId, transferType, originService, originType, originReference, orderingPartyReference, receivedAt, sourceAccountId, destinationAccountId, acceptedDestination, principalAmount, currency, purposeCode, authorizationBasis, acceptedContentHash, concepts, ProcessingState.RECEIVED, false, List.of(), List.of(), List.of(), 1L, 0L, createdActionId, createdAt, createdAt, List.of());
  }

  public static Transfer restore(UUID transferId, TransferType transferType, String originService, String originType, String originReference, ObjectReference orderingPartyReference, Instant receivedAt, UUID sourceAccountId, UUID destinationAccountId, AcceptedDestination acceptedDestination, BigDecimal principalAmount, Currency currency, String purposeCode, AuthorizationBasis authorizationBasis, String acceptedContentHash, List<MonetaryConcept> concepts, ProcessingState processingState, boolean dispatchBarrier, List<PendingComponent> pendingComponents, List<ConfirmedFact> confirmedFacts, List<ObjectReference> monetaryEffectReferences, long version, long lockVersion, UUID createdActionId, Instant createdAt, Instant updatedAt) {
    return new Transfer(transferId, transferType, originService, originType, originReference, orderingPartyReference, receivedAt, sourceAccountId, destinationAccountId, acceptedDestination, principalAmount, currency, purposeCode, authorizationBasis, acceptedContentHash, concepts, processingState, dispatchBarrier, pendingComponents, confirmedFacts, monetaryEffectReferences, version, lockVersion, createdActionId, createdAt, updatedAt, List.of());
  }

  public static Transfer restore(UUID transferId, TransferType transferType, String originService, String originType, String originReference, ObjectReference orderingPartyReference, Instant receivedAt, UUID sourceAccountId, UUID destinationAccountId, AcceptedDestination acceptedDestination, BigDecimal principalAmount, Currency currency, String purposeCode, AuthorizationBasis authorizationBasis, String acceptedContentHash, List<MonetaryConcept> concepts, ProcessingState processingState, boolean dispatchBarrier, List<PendingComponent> pendingComponents, List<ConfirmedFact> confirmedFacts, List<ObjectReference> monetaryEffectReferences, long version, long lockVersion, UUID createdActionId, Instant createdAt, Instant updatedAt, List<ObjectReference> rejectionEvidenceReferences) {
    return new Transfer(transferId, transferType, originService, originType, originReference, orderingPartyReference, receivedAt, sourceAccountId, destinationAccountId, acceptedDestination, principalAmount, currency, purposeCode, authorizationBasis, acceptedContentHash, concepts, processingState, dispatchBarrier, pendingComponents, confirmedFacts, monetaryEffectReferences, version, lockVersion, createdActionId, createdAt, updatedAt, rejectionEvidenceReferences);
  }

  public void acceptInstruction(AcceptedDestination destination) {
    Objects.requireNonNull(destination, "destination");
    if (destination.currency() != currency)
      throw new DomainInvariantException("La moneda del destino debe coincidir con la transferencia.");
    if (authorizationBasis != null && !authorizationBasis.contentHash().equalsIgnoreCase(acceptedContentHash))
      throw new DomainInvariantException("El destino no está cubierto por la autorización vigente.");
    if (this.acceptedDestination != null && !this.acceptedDestination.equals(destination))
      throw new DomainInvariantException("El destino aceptado es inmutable.");
    if (this.transferType == TransferType.INCOMING || (this.transferType == TransferType.INTERNAL && destination.destinationType() != DestinationType.INTERNAL) || (this.transferType == TransferType.OUTGOING && destination.destinationType() != DestinationType.EXTERNAL))
      throw new DomainInvariantException("El destino no corresponde al tipo de transferencia.");
    this.acceptedDestination = destination;
    this.updatedAt = Instant.now();
    if (this.processingState == ProcessingState.RECEIVED) {
      this.processingState = ProcessingState.VALIDATING;
    }
  }

  public void acceptInstruction(AcceptedDestination destination, String canonicalDestinationHash) {
    Objects.requireNonNull(destination, "destination");
    if (canonicalDestinationHash == null || !canonicalDestinationHash.matches("^[a-fA-F0-9]{64}$") || !canonicalDestinationHash.equalsIgnoreCase(destination.acceptedContentHash()))
      throw new DomainInvariantException("El destino no coincide con su contenido canónico.");
    acceptInstruction(destination);
  }

  public void fixAcceptedDestination(AcceptedDestination destination) {
    acceptInstruction(destination);
  }

  public void recordAuthorization(AuthorizationBasis basis) {
    Objects.requireNonNull(basis, "basis");
    if (!acceptedContentHash.equalsIgnoreCase(basis.contentHash()))
      throw new DomainInvariantException("La autorización no corresponde al contenido completo de la instrucción.");
    this.authorizationBasis = basis;
    this.updatedAt = Instant.now();
  }

  public void recordAuthorization(AuthorizationBasis basis, String canonicalContent, long canonicalizationVersion) {
    Objects.requireNonNull(canonicalContent, "canonicalContent");
    if (canonicalizationVersion <= 0) throw new DomainInvariantException("canonicalizationVersion debe ser positiva.");
    if (!sha256(canonicalContent).equalsIgnoreCase(acceptedContentHash))
      throw new DomainInvariantException("El hash de Transfer no coincide con el contenido canónico completo.");
    recordAuthorization(basis);
    this.authorizedCanonicalContent = canonicalContent;
    this.canonicalizationVersion = canonicalizationVersion;
  }

  public void verifyAcceptedDestination(String canonicalDestinationContent) {
    if (acceptedDestination == null) throw new DomainInvariantException("No hay destino aceptado que verificar.");
    if (canonicalDestinationContent == null || !sha256(canonicalDestinationContent).equalsIgnoreCase(acceptedDestination.acceptedContentHash()))
      throw new DomainInvariantException("El hash del destino no coincide con su contenido canónico.");
  }

  public void verifyAuthorizedContent(String canonicalInstructionContent, long canonicalizationVersion) {
    if (canonicalInstructionContent == null || canonicalizationVersion <= 0)
      throw new DomainInvariantException("Se requiere contenido canónico y versión positiva.");
    if (!sha256(canonicalInstructionContent).equalsIgnoreCase(acceptedContentHash))
      throw new DomainInvariantException("El contenido canónico no coincide con la instrucción autorizada.");
    if (this.canonicalizationVersion != canonicalizationVersion)
      throw new DomainInvariantException("La versión de canonicalización no coincide con la aceptada.");
  }

  private static String sha256(String content) {
    try {
      byte[] digest = java.security.MessageDigest.getInstance("SHA-256").digest(content.getBytes(java.nio.charset.StandardCharsets.UTF_8));
      return java.util.HexFormat.of().formatHex(digest);
    } catch (java.security.NoSuchAlgorithmException impossible) {
      throw new IllegalStateException("SHA-256 no está disponible.", impossible);
    }
  }

  public void recordMonetaryOutcome(TransferMonetaryExecution execution) {
    Objects.requireNonNull(execution, "execution");
    if (!transferId.equals(execution.getTransferId()))
      throw new DomainInvariantException("La ejecución monetaria pertenece a otra transferencia.");
    if (execution.getStatus() == com.lab.transferencias.domain.enums.MonetaryExecutionStatus.CONFIRMED && execution.getMovementReferences().isEmpty())
      throw new DomainInvariantException("Una ejecución confirmada requiere referencias de movimientos.");
    for (ObjectReference ref : execution.getMovementReferences()) recordMonetaryOutcome(ref);
    if (execution.getStatus() == com.lab.transferencias.domain.enums.MonetaryExecutionStatus.CONFIRMED && transferType == TransferType.INTERNAL && (execution.getDebitEffectId() == null || execution.getCreditEffectId() == null || execution.getMovementGroupId() == null))
      throw new DomainInvariantException("La transferencia interna requiere débito y crédito conjuntos identificados.");
    if (concepts.stream().anyMatch(c -> c.currency() != currency) && execution.getFxReference() == null)
      throw new DomainInvariantException("La ejecución con importes en otra moneda requiere referencia cambiaria.");
  }

  public void recordIncomingAcceptance(TransferIncomingAcceptance acceptance) {
    Objects.requireNonNull(acceptance, "acceptance");
    if (transferType != TransferType.INCOMING || !transferId.equals(acceptance.getTransferId()))
      throw new DomainInvariantException("La aceptación entrante no corresponde a esta transferencia.");
    if (acceptance.getAcceptanceStatus() == com.lab.transferencias.domain.enums.IncomingAcceptanceStatus.ACCEPTED && acceptance.getCreditEffectId() == null)
      throw new DomainInvariantException("La aceptación firme requiere identidad de crédito.");
    attachIncomingAcceptance(acceptance);
  }

  public void recordMonetaryOutcome(ObjectReference reference) {
    if (reference == null) {
      throw new DomainInvariantException("reference no puede ser nula.");
    }
    if (this.monetaryEffectReferences.contains(reference)) return;
    ArrayList<ObjectReference> updated = new ArrayList<>(this.monetaryEffectReferences);
    updated.add(reference);
    this.monetaryEffectReferences = Collections.unmodifiableList(updated);
    this.updatedAt = Instant.now();
  }

  public void recordConfirmedFact(ConfirmedFact fact) {
    Objects.requireNonNull(fact, "fact");
    for (ConfirmedFact existing : this.confirmedFacts) {
      if (existing.factType() == fact.factType() && existing.sourceReference().equals(fact.sourceReference())) {
        if (!existing.equals(fact))
          throw new DomainInvariantException("El mismo hecho fuente se repitió con contenido distinto.");
        return;
      }
    }
    ArrayList<ConfirmedFact> updated = new ArrayList<>(this.confirmedFacts);
    updated.add(fact);
    this.confirmedFacts = Collections.unmodifiableList(updated);
    this.updatedAt = Instant.now();
  }

  public void recordPendingComponent(PendingComponent component) {
    Objects.requireNonNull(component, "component");
    for (PendingComponent existing : this.pendingComponents) {
      if (existing.componentCode().equals(component.componentCode())) {
        if (!existing.equals(component))
          throw new DomainInvariantException("El componente pendiente ya existe con identidad o motivo distinto.");
        return;
      }
    }
    ArrayList<PendingComponent> updated = new ArrayList<>(this.pendingComponents);
    updated.add(component);
    this.pendingComponents = Collections.unmodifiableList(updated);
    this.updatedAt = Instant.now();
  }

  public void protectDispatch() {
    if (authorizationBasis == null)
      throw new DomainInvariantException("El despacho requiere fundamento de autorización asociado al contenido.");
    if (dispatchBarrier) {
      throw new DomainInvariantException("La transferencia está protegida frente al despacho.");
    }
    if (processingState == ProcessingState.CANCELLED) {
      throw new DomainInvariantException("No se puede despachar una transferencia cancelada.");
    }
    if (transferType == TransferType.OUTGOING) {
      if (outgoingExecution == null)
        throw new DomainInvariantException("Falta la cobertura de la transferencia saliente.");
      outgoingExecution.protectCoverage();
      if (outgoingExecution.getCoverageStatus() != com.lab.transferencias.domain.enums.OutgoingCoverageStatus.PROTECTED)
        throw new DomainInvariantException("La cobertura debe estar protegida antes del despacho.");
    }
  }

  public void attachOutgoingExecution(TransferOutgoingExecution execution) {
    Objects.requireNonNull(execution, "execution");
    if (transferType != TransferType.OUTGOING || !transferId.equals(execution.getTransferId()))
      throw new DomainInvariantException("La ejecución no corresponde a esta transferencia saliente.");
    if (outgoingExecution != null) {
      if (outgoingExecution.getMechanismReference().equals(execution.getMechanismReference()) && outgoingExecution.getReservationReference().equals(execution.getReservationReference()) && outgoingExecution.getConsumptionStepId().equals(execution.getConsumptionStepId()))
        return;
      throw new DomainInvariantException("Ya existe una ejecución saliente con otra identidad de reserva.");
    }
    outgoingExecution = execution;
  }

  public void attachReturnControl(TransferReturnControl control) {
    Objects.requireNonNull(control, "control");
    if (!transferId.equals(control.getTransferId()) || control.getOriginalPrincipal().compareTo(principalAmount) != 0)
      throw new DomainInvariantException("El control de devolución no corresponde al principal de esta transferencia.");
    if (returnControl != null && returnControl != control)
      throw new DomainInvariantException("El control de devolución ya está asociado.");
    if (returnControl != null && control.getLockVersion() < returnControl.getLockVersion())
      throw new DomainInvariantException("La versión del control de devolución no puede retroceder.");
    returnControl = control;
  }

  public void registerReturn(TransferReturn transferReturn, UUID expectedReturnEffectId) {
    Objects.requireNonNull(transferReturn, "transferReturn");
    if (expectedReturnEffectId == null || !expectedReturnEffectId.equals(transferReturn.getReturnEffectId()))
      throw new DomainInvariantException("La identidad económica de la devolución no coincide.");
    TransferReturn duplicate = returns.stream().filter(r -> r.getReturnEffectId().equals(expectedReturnEffectId)).findFirst().orElse(null);
    if (duplicate != null) {
      if (!duplicate.sameRequestContent(transferReturn))
        throw new DomainInvariantException("La identidad de devolución ya existe con contenido distinto.");
      return;
    }
    if (returnControl == null) throw new DomainInvariantException("Falta el control de remanente de devoluciones.");
    if (!confirmedFacts.stream().anyMatch(f -> f.factType() == com.lab.transferencias.domain.enums.ConfirmedFactType.BENEFICIARY_CREDITED))
      throw new DomainInvariantException("La transferencia debe tener crédito confirmado antes de devolver.");
    if (transferReturn.getStatus() == com.lab.transferencias.domain.enums.ReturnStatus.CONFIRMED)
      throw new DomainInvariantException("La devolución debe comprometerse antes de ejecutar su efecto.");
    if (!transferId.equals(transferReturn.getTransferId()) || transferReturn.getCurrency() != currency)
      throw new DomainInvariantException("La devolución no corresponde a esta transferencia y moneda.");
    transferReturn.validateDestinationCurrency();
    if (transferReturn.getAuthorizationBasis() == null)
      throw new DomainInvariantException("La devolución requiere fundamento autorizado.");
    returnControl.commitReturnAmount(transferReturn.getAmount());
    transferReturn.registerReturn();
    ArrayList<TransferReturn> copy = new ArrayList<>(returns);
    copy.add(transferReturn);
    returns = Collections.unmodifiableList(copy);
    updatedAt = Instant.now();
  }

  public void recordReturnUncertainty(UUID returnEffectId, List<ObjectReference> evidence) {
    TransferReturn item = returns.stream().filter(r -> r.getReturnEffectId().equals(returnEffectId)).findFirst().orElseThrow(() -> new DomainInvariantException("No existe la devolución indicada."));
    item.recordUncertainty(evidence);
  }

  public TransferReturn findReturnByEffectId(UUID effectId) {
    if (effectId == null) throw new DomainInvariantException("returnEffectId es obligatorio.");
    return returns.stream().filter(r -> r.getReturnEffectId().equals(effectId)).findFirst().orElse(null);
  }

  public void resolveReturn(UUID returnEffectId, com.lab.transferencias.domain.enums.ReturnStatus resolution, List<ObjectReference> evidence) {
    if (resolution == null) throw new DomainInvariantException("resolution es obligatoria.");
    TransferReturn item = findReturnByEffectId(returnEffectId);
    if (item == null) throw new DomainInvariantException("No existe la devolución indicada.");
    switch (resolution) {
      case CONFIRMED -> {
        if (evidence == null || evidence.isEmpty())
          throw new DomainInvariantException("La confirmación requiere evidencia.");
        if (item.getStatus() == com.lab.transferencias.domain.enums.ReturnStatus.CONFIRMED) return;
        if (item.getStatus() == com.lab.transferencias.domain.enums.ReturnStatus.REJECTED)
          throw new DomainInvariantException("Una devolución rechazada no puede confirmarse.");
        returnControl.confirmReturnAmount(item.getAmount());
        item.confirmWithEvidence(evidence);
        recordConfirmedFact(new ConfirmedFact(com.lab.transferencias.domain.enums.ConfirmedFactType.RETURNED, Instant.now(), evidence.get(0), evidence));
      }
      case REJECTED -> {
        if (evidence == null || evidence.isEmpty())
          throw new DomainInvariantException("El rechazo requiere evidencia.");
        if (item.getStatus() == com.lab.transferencias.domain.enums.ReturnStatus.CONFIRMED)
          throw new DomainInvariantException("Una devolución confirmada no puede rechazarse.");
        if (item.getStatus() == com.lab.transferencias.domain.enums.ReturnStatus.REJECTED) return;
        returnControl.releaseRejectedReturn(item.getAmount());
        item.rejectWithEvidence(evidence);
      }
      case UNCERTAIN -> item.recordUncertainty(evidence);
      case REQUIRES_REVIEW -> item.requireReview(evidence);
      case PENDING -> throw new DomainInvariantException("Una resolución no puede volver a PENDING.");
    }
    updatedAt = Instant.now();
  }

  public void addConfirmedFact(ConfirmedFact fact) {
    Objects.requireNonNull(fact, "fact");
    if (fact.factType() == com.lab.transferencias.domain.enums.ConfirmedFactType.BENEFICIARY_CREDITED && confirmedFacts.stream().anyMatch(existing -> existing.factType() == com.lab.transferencias.domain.enums.ConfirmedFactType.DISPATCHED && existing.sourceReference().equals(fact.sourceReference())))
      throw new DomainInvariantException("El despacho no puede usarse como evidencia de acreditación.");
    if (fact.factType() == com.lab.transferencias.domain.enums.ConfirmedFactType.BENEFICIARY_CREDITED && confirmedFacts.stream().anyMatch(existing -> existing.factType() == com.lab.transferencias.domain.enums.ConfirmedFactType.BENEFICIARY_CREDITED))
      throw new DomainInvariantException("El crédito ya fue registrado; no se debe duplicar.");
    recordConfirmedFact(fact);
  }

  public void registerDispatchFact(ConfirmedFact fact) {
    if (fact == null || fact.factType() != com.lab.transferencias.domain.enums.ConfirmedFactType.DISPATCHED)
      throw new DomainInvariantException("Se requiere evidencia del hecho DISPATCHED.");
    if (dispatchBarrier || processingState == ProcessingState.CANCELLED)
      throw new DomainInvariantException("La barrera de cancelación impide el despacho.");
    if (transferType == TransferType.OUTGOING) {
      protectDispatch();
      if (outgoingExecution.getDispatchAuthorizedAt() == null || outgoingExecution.getExchangeReference() == null)
        throw new DomainInvariantException("El despacho comprobado requiere intención de intercambio previamente registrada.");
    }
    recordConfirmedFact(fact);
    if (processingState == ProcessingState.PREPARING || processingState == ProcessingState.VALIDATING)
      processingState = ProcessingState.EXECUTING;
  }

  public void attachCancellation(TransferCancellation cancellation) {
    Objects.requireNonNull(cancellation, "cancellation");
    if (!transferId.equals(cancellation.getTransferId()))
      throw new DomainInvariantException("La cancelación pertenece a otra transferencia.");
    TransferCancellation prior = cancellations.stream().filter(c -> c.getCancellationId().equals(cancellation.getCancellationId())).findFirst().orElse(null);
    if (prior != null) {
      if (!prior.sameRequestContent(cancellation))
        throw new DomainInvariantException("La identidad de cancelación se repitió con contenido distinto.");
      if (prior.getOutcome() != cancellation.getOutcome() || !prior.getFinalEvidence().equals(cancellation.getFinalEvidence()))
        throw new DomainInvariantException("La repetición de cancelación trae un resultado distinto al ya registrado.");
      return;
    }
    if (cancellation.getOutcome() == CancellationOutcome.CANCELLED && confirmedFacts.stream().anyMatch(f -> f.factType() == com.lab.transferencias.domain.enums.ConfirmedFactType.BENEFICIARY_CREDITED))
      throw new DomainInvariantException("Una transferencia acreditada requiere devolución, no cancelación.");
    if (cancellation.getOutcome() == CancellationOutcome.CANCELLED && !cancellation.getFinalEvidence().isEmpty()) {
      dispatchBarrier = true;
    }
    ArrayList<TransferCancellation> copy = new ArrayList<>(cancellations);
    copy.add(cancellation);
    cancellations = Collections.unmodifiableList(copy);
    applyCancellationOutcome(cancellation);
  }

  public void attachRemoteStep(TransferRemoteStep step) {
    Objects.requireNonNull(step, "step");
    if (!transferId.equals(step.getTransferId()))
      throw new DomainInvariantException("El paso remoto pertenece a otra transferencia.");
    if (remoteSteps.stream().anyMatch(s -> s.getComponentCode().equals(step.getComponentCode())))
      throw new DomainInvariantException("Ya existe un paso para ese componente.");
    if (remoteSteps.stream().anyMatch(s -> s.hasSameRemoteIdentity(step.getRemoteActionId(), step.getResponsibleService())))
      throw new DomainInvariantException("La identidad de actuación remota ya está asociada.");
    ArrayList<TransferRemoteStep> copy = new ArrayList<>(remoteSteps);
    copy.add(step);
    remoteSteps = Collections.unmodifiableList(copy);
  }

  public void addRemoteStep(TransferRemoteStep step) {
    attachRemoteStep(step);
  }

  public void attachReturnStep(UUID returnId, TransferReturnStep step) {
    Objects.requireNonNull(step, "step");
    if (!Objects.equals(returnId, step.getReturnId()) || returns.stream().noneMatch(r -> r.getReturnId().equals(returnId)))
      throw new DomainInvariantException("El paso no pertenece a una devolución registrada.");
    TransferReturn owner = returns.stream().filter(r -> r.getReturnId().equals(returnId)).findFirst().orElseThrow();
    owner.attachStep(step);
  }

  public void attachMonetaryExecution(TransferMonetaryExecution execution) {
    Objects.requireNonNull(execution, "execution");
    TransferMonetaryExecution existing = monetaryExecutions.stream().filter(e -> e.getFundsService().equals(execution.getFundsService()) && e.getRemoteActionId().equals(execution.getRemoteActionId())).findFirst().orElse(null);
    if (existing != null) {
      if (!existing.getRequestedContent().contentHash().equals(execution.getRequestedContent().contentHash()))
        throw new DomainInvariantException("La misma actuación de Fondos se repitió con contenido distinto.");
      return;
    }
    if (!monetaryExecutions.isEmpty())
      throw new DomainInvariantException("La transferencia ya tiene una ejecución monetaria identificada; la recuperación debe reutilizarla.");
    ArrayList<TransferMonetaryExecution> copy = new ArrayList<>(monetaryExecutions);
    copy.add(execution);
    monetaryExecutions = Collections.unmodifiableList(copy);
    monetaryExecution = execution;
    recordMonetaryOutcome(execution);
  }

  public void attachIncomingAcceptance(TransferIncomingAcceptance acceptance) {
    Objects.requireNonNull(acceptance, "acceptance");
    if (transferType != TransferType.INCOMING || !transferId.equals(acceptance.getTransferId()))
      throw new DomainInvariantException("La aceptación no corresponde a esta transferencia entrante.");
    if (incomingAcceptance != null) {
      if (incomingAcceptance.sameRequestContent(acceptance)) return;
      throw new DomainInvariantException("Ya existe una recepción entrante incompatible.");
    }
    incomingAcceptance = acceptance;
  }

  public void attachExternalOperation(TransferExternalOperation operation) {
    Objects.requireNonNull(operation, "operation");
    if (transferType == TransferType.INTERNAL)
      throw new DomainInvariantException("Una transferencia interna no tiene operación externa.");
    if (!transferId.equals(operation.getTransferId()))
      throw new DomainInvariantException("La operación externa pertenece a otra transferencia.");
    TransferExternalOperation duplicate = externalOperations.stream().filter(existing -> existing.matchesExternalIdentity(operation.getParticipantId(), operation.getMechanismCode(), operation.getExternalOperationId())).findFirst().orElse(null);
    if (duplicate != null) {
      if (duplicate.verifyContentConsistency(operation.getExternalContentHash())) return;
      throw new DomainInvariantException("La identidad externa ya está asociada con contenido incompatible.");
    }
    if (!externalOperations.isEmpty())
      throw new DomainInvariantException("La transferencia ya tiene una identidad externa asociada.");
    ArrayList<TransferExternalOperation> copy = new ArrayList<>(externalOperations);
    copy.add(operation);
    externalOperations = Collections.unmodifiableList(copy);
    externalOperation = operation;
  }

  public TransferMonetaryExecution getMonetaryExecution() {
    return monetaryExecution;
  }

  public List<TransferMonetaryExecution> getMonetaryExecutions() {
    return monetaryExecutions;
  }

  public TransferIncomingAcceptance getIncomingAcceptance() {
    return incomingAcceptance;
  }

  public TransferExternalOperation getExternalOperation() {
    return externalOperation;
  }

  public List<TransferExternalOperation> getExternalOperations() {
    return externalOperations;
  }

  public TransferOutgoingExecution getOutgoingExecution() {
    return outgoingExecution;
  }

  public void updateReturnControlFromConfirmed(TransferReturnControl updated) {
    Objects.requireNonNull(updated, "updated");
    if (!transferId.equals(updated.getTransferId()) || updated.getOriginalPrincipal().compareTo(principalAmount) != 0)
      throw new DomainInvariantException("El control actualizado no corresponde a esta transferencia.");
    if (returnControl != null && updated.getLockVersion() < returnControl.getLockVersion())
      throw new DomainInvariantException("No se puede retroceder la versión del control de devoluciones.");
    returnControl = updated;
  }

  public void recordContentChange(String newContentHash, AuthorizationBasis renewedAuthorization) {
    throw new DomainInvariantException("Para cambiar contenido se requiere suministrar el contenido canónico, no solo su hash.");
  }

  public void recordCanonicalContentChange(String canonicalContent, long canonicalizationVersion, AuthorizationBasis renewedAuthorization) {
    if (canonicalContent == null || canonicalizationVersion <= 0)
      throw new DomainInvariantException("Se requiere contenido canónico y versión positiva.");
    String newContentHash = sha256(canonicalContent);
    validateContentHash(newContentHash);
    Objects.requireNonNull(renewedAuthorization, "renewedAuthorization");
    if (!newContentHash.equalsIgnoreCase(renewedAuthorization.contentHash()))
      throw new DomainInvariantException("La autorización debe cubrir el nuevo contenido.");
    if (confirmedFacts.stream().anyMatch(f -> f.factType() == com.lab.transferencias.domain.enums.ConfirmedFactType.DISPATCHED || f.factType() == com.lab.transferencias.domain.enums.ConfirmedFactType.BENEFICIARY_CREDITED))
      throw new DomainInvariantException("No se puede cambiar contenido después del despacho o acreditación.");
    if (acceptedDestination != null)
      throw new DomainInvariantException("Un cambio material de contenido requiere una nueva transferencia aceptada.");
    acceptedContentHash = newContentHash;
    authorizationBasis = renewedAuthorization;
    authorizedCanonicalContent = canonicalContent;
    this.canonicalizationVersion = canonicalizationVersion;
    version++;
    updatedAt = Instant.now();
  }

  public void recordContentChange(String newContentHash, long canonicalizationVersion, AuthorizationBasis renewedAuthorization) {
    throw new DomainInvariantException("Para cambiar contenido se requiere suministrar el contenido canónico, no solo su hash.");
  }

  public void restoreCanonicalizationVersion(long value) {
    if (value <= 0) throw new DomainInvariantException("canonicalizationVersion debe ser positiva.");
    this.canonicalizationVersion = value;
  }

  public long getCanonicalizationVersion() {
    return canonicalizationVersion;
  }

  public void validateMonetaryConcepts() {
    validatePrincipalConcept();
    if (monetaryExecution != null) validateCurrencyConversion();
  }

  private void validatePrincipalConcept() {
    long principals = concepts.stream().filter(c -> c.componentType() == com.lab.transferencias.domain.enums.MonetaryComponentType.PRINCIPAL).count();
    if (principals != 1) throw new DomainInvariantException("Debe existir exactamente un concepto PRINCIPAL.");
    for (MonetaryConcept concept : concepts) {
      if (concept.componentType() == com.lab.transferencias.domain.enums.MonetaryComponentType.PRINCIPAL && (concept.amount().compareTo(principalAmount) != 0 || concept.currency() != currency))
        throw new DomainInvariantException("El concepto principal debe coincidir con principalAmount y currency.");
    }
  }

  public boolean matchesOriginIdentity(String originService, String originType, String originReference) {
    return this.originService.equals(originService) && this.originType.equals(originType) && this.originReference.equals(originReference);
  }

  public boolean verifyOriginContentConsistency(String candidateContentHash) {
    if (!matchesContentHash(candidateContentHash))
      throw new DomainInvariantException("La identidad de origen ya existe con contenido distinto.");
    return true;
  }

  private boolean matchesContentHash(String candidateContentHash) {
    return candidateContentHash != null && acceptedContentHash.equalsIgnoreCase(candidateContentHash);
  }

  public void updateProcessingState(ProcessingState next) {
    Objects.requireNonNull(next, "next");
    boolean allowed = switch (processingState) {
      case RECEIVED -> next == ProcessingState.VALIDATING || next == ProcessingState.REJECTED;
      case VALIDATING ->
          next == ProcessingState.PREPARING || next == ProcessingState.REJECTED || next == ProcessingState.REQUIRES_RESOLUTION;
      case PREPARING ->
          next == ProcessingState.EXECUTING || next == ProcessingState.REJECTED || next == ProcessingState.CANCELLED || next == ProcessingState.REQUIRES_RESOLUTION;
      case EXECUTING ->
          next == ProcessingState.COMPLETED || next == ProcessingState.CANCELLED || next == ProcessingState.REJECTED || next == ProcessingState.REQUIRES_RESOLUTION;
      case REQUIRES_RESOLUTION ->
          next == ProcessingState.EXECUTING || next == ProcessingState.COMPLETED || next == ProcessingState.CANCELLED || next == ProcessingState.REJECTED;
      case COMPLETED, CANCELLED, REJECTED -> false;
    };
    if (!allowed)
      throw new DomainInvariantException("Transición de estado incompatible: " + processingState + " -> " + next);
    if (next == ProcessingState.COMPLETED && confirmedFacts.isEmpty())
      throw new DomainInvariantException("No se puede completar sin hechos confirmados.");
    if (next == ProcessingState.COMPLETED) validateCompletionCriteria();
    if (next == ProcessingState.REJECTED && rejectionEvidenceReferences.isEmpty())
      throw new DomainInvariantException("REJECTED requiere rechazo definitivo con evidencia suficiente.");
    if (next == ProcessingState.CANCELLED && !dispatchBarrier)
      throw new DomainInvariantException("La cancelación debe activar la barrera de despacho.");
    processingState = next;
    version++;
    updatedAt = Instant.now();
  }

  public void markCompleted() {
    validateCompletionCriteria();
    updateProcessingState(ProcessingState.COMPLETED);
  }

  private void validateCompletionCriteria() {
    validateMonetaryConcepts();
    if (transferType == TransferType.INTERNAL && (monetaryExecution == null || monetaryExecution.getStatus() != com.lab.transferencias.domain.enums.MonetaryExecutionStatus.CONFIRMED))
      throw new DomainInvariantException("La transferencia interna requiere ejecución monetaria confirmada.");
    if (transferType == TransferType.INTERNAL && confirmedFacts.stream().noneMatch(f -> f.factType() == com.lab.transferencias.domain.enums.ConfirmedFactType.INSTRUMENT_EXECUTED))
      throw new DomainInvariantException("La transferencia interna requiere el hecho INSTRUMENT_EXECUTED.");
    if (transferType == TransferType.OUTGOING && (outgoingExecution == null || outgoingExecution.getBeneficiaryCreditedAt() == null))
      throw new DomainInvariantException("La transferencia saliente requiere crédito de beneficiario comprobado.");
    if (transferType == TransferType.INCOMING && (incomingAcceptance == null || incomingAcceptance.getAcceptanceStatus() != com.lab.transferencias.domain.enums.IncomingAcceptanceStatus.ACCEPTED || monetaryExecution == null || monetaryExecution.getStatus() != com.lab.transferencias.domain.enums.MonetaryExecutionStatus.CONFIRMED))
      throw new DomainInvariantException("La transferencia entrante requiere aceptación y crédito confirmados.");
    if ((transferType == TransferType.OUTGOING || transferType == TransferType.INCOMING) && confirmedFacts.stream().noneMatch(f -> f.factType() == com.lab.transferencias.domain.enums.ConfirmedFactType.BENEFICIARY_CREDITED))
      throw new DomainInvariantException("La transferencia externa requiere el hecho BENEFICIARY_CREDITED.");
  }

  public void rejectWithEvidence(List<ObjectReference> evidenceReferences) {
    if (evidenceReferences == null || evidenceReferences.isEmpty())
      throw new DomainInvariantException("El rechazo definitivo requiere evidencia.");
    if (processingState == ProcessingState.COMPLETED || processingState == ProcessingState.CANCELLED)
      throw new DomainInvariantException("Una transferencia completada o cancelada no puede rechazarse.");
    this.rejectionEvidenceReferences = immutableObjectReferences(evidenceReferences);
    updateProcessingState(ProcessingState.REJECTED);
  }

  public boolean hasCurrencyConversion() {
    return concepts.stream().anyMatch(c -> c.currency() != currency) && (monetaryExecution == null || monetaryExecution.getFxReference() == null);
  }

  public void validateCurrencyConversion() {
    if (hasCurrencyConversion())
      throw new DomainInvariantException("La conversión de moneda requiere referencia cambiaria.");
  }

  public List<ObjectReference> getRejectionEvidenceReferences() {
    return rejectionEvidenceReferences;
  }

  public void authorizeDispatch() {
    protectDispatch();
    if (outgoingExecution == null) throw new DomainInvariantException("Falta la ejecución saliente.");
    if (outgoingExecution.getExchangeReference() == null)
      throw new DomainInvariantException("Debe registrarse la intención de intercambio antes del despacho.");
    outgoingExecution.authorizeDispatch(Instant.now());
  }

  public void authorizeDispatch(ObjectReference exchangeReference) {
    protectDispatch();
    if (outgoingExecution == null) throw new DomainInvariantException("Falta la ejecución saliente.");
    outgoingExecution.authorizeDispatch(Instant.now(), exchangeReference);
  }

  public boolean registerFact(TransferFact fact) {
    Objects.requireNonNull(fact, "fact");
    if (!transferId.equals(fact.getTransferId()))
      throw new DomainInvariantException("El hecho pertenece a otra transferencia.");
    TransferFact prior = facts.stream().filter(existing -> existing.matchesOriginIdentity(fact.getSourceService(), fact.getSourceFactId(), fact.getSourceFactVersion())).findFirst().orElse(null);
    if (prior != null) {
      if (!prior.sameOriginContent(fact))
        throw new DomainInvariantException("La identidad y versión del hecho de origen se repitió con contenido distinto.");
      return false;
    }
    ArrayList<TransferFact> copy = new ArrayList<>(facts);
    copy.add(fact);
    facts = Collections.unmodifiableList(copy);
    return true;
  }

  public List<TransferReturn> getReturns() {
    return returns;
  }

  public List<TransferCancellation> getCancellations() {
    return cancellations;
  }

  public List<TransferRemoteStep> getRemoteSteps() {
    return remoteSteps;
  }

  public List<TransferFact> getFacts() {
    return facts;
  }

  public TransferReturnControl getReturnControl() {
    return returnControl;
  }

  public void applyCancellation(TransferCancellation cancellation) {
    attachCancellation(cancellation);
  }

  private void applyCancellationOutcome(TransferCancellation cancellation) {
    Objects.requireNonNull(cancellation, "cancellation");
    if (!transferId.equals(cancellation.getTransferId()))
      throw new DomainInvariantException("La cancelación pertenece a otra transferencia.");
    if ((cancellation.getOutcome() == CancellationOutcome.CANCELLED || cancellation.getOutcome() == CancellationOutcome.ALREADY_CREDITED) && cancellation.getFinalEvidence().isEmpty())
      throw new DomainInvariantException("El resultado requiere evidencia final.");
    if (cancellation.getOutcome() == CancellationOutcome.CANCELLED || cancellation.getOutcome() == CancellationOutcome.ALREADY_CREDITED) {
      this.dispatchBarrier = true;
    }
    if (this.processingState != ProcessingState.CANCELLED && cancellation.getOutcome() == CancellationOutcome.CANCELLED) {
      this.processingState = ProcessingState.CANCELLED;
    }
    this.updatedAt = Instant.now();
  }

  public boolean canCancelAsUnexecuted() {
    return confirmedFacts.stream().noneMatch(f -> f.factType() == com.lab.transferencias.domain.enums.ConfirmedFactType.DISPATCHED || f.factType() == com.lab.transferencias.domain.enums.ConfirmedFactType.BENEFICIARY_CREDITED);
  }

  public void registerReturn(TransferReturn transferReturn) {
    registerReturn(transferReturn, transferReturn == null ? null : transferReturn.getReturnEffectId());
  }

  private static void validateRequiredFields(UUID transferId, TransferType transferType, String originService, String originType, String originReference, ObjectReference orderingPartyReference, Instant receivedAt, BigDecimal principalAmount, Currency currency, String purposeCode, String acceptedContentHash, UUID createdActionId, Instant createdAt, Instant updatedAt) {
    if (transferId == null) {
      throw new DomainInvariantException("transferId es obligatorio.");
    }
    if (transferType == null) {
      throw new DomainInvariantException("transferType es obligatorio.");
    }
    if (originService == null || originService.isBlank()) {
      throw new DomainInvariantException("originService no puede ser nulo ni vacío.");
    }
    if (originService.length() > 80)
      throw new DomainInvariantException("originService no puede exceder 80 caracteres.");
    if (originType == null || originType.isBlank()) {
      throw new DomainInvariantException("originType no puede ser nulo ni vacío.");
    }
    if (originType.length() > 80) throw new DomainInvariantException("originType no puede exceder 80 caracteres.");
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
    if (purposeCode.length() > 80) throw new DomainInvariantException("purposeCode no puede exceder 80 caracteres.");
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

  private static void validateTypeSpecificRules(TransferType transferType, UUID sourceAccountId, UUID destinationAccountId, AcceptedDestination acceptedDestination) {
    if (transferType == TransferType.INTERNAL) {
      if (sourceAccountId == null || destinationAccountId == null) {
        throw new DomainInvariantException("Para INTERNAL deben existir originAccountId y destinationAccountId.");
      }
      if (sourceAccountId.equals(destinationAccountId)) {
        throw new DomainInvariantException("Las cuentas de origen y destino no pueden ser iguales.");
      }
      if (acceptedDestination == null || acceptedDestination.destinationType() != com.lab.transferencias.domain.enums.DestinationType.INTERNAL)
        throw new DomainInvariantException("INTERNAL requiere destino interno aceptado.");
    }
    if (transferType == TransferType.OUTGOING && (acceptedDestination == null || sourceAccountId == null || acceptedDestination.destinationType() != com.lab.transferencias.domain.enums.DestinationType.EXTERNAL)) {
      throw new DomainInvariantException("OUTGOING requiere cuenta de origen y destino externo aceptado.");
    }
    if (transferType == TransferType.INCOMING && destinationAccountId == null) {
      throw new DomainInvariantException("INCOMING requiere destinationAccountId.");
    }
    if (transferType == TransferType.INCOMING && acceptedDestination != null)
      throw new DomainInvariantException("INCOMING no utiliza AcceptedDestination.");
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
      if (item == null)
        throw new DomainInvariantException("monetaryEffectReferences no puede contener elementos nulos.");
      copy.add(item);
    }
    return Collections.unmodifiableList(copy);
  }

  public UUID getTransferId() {
    return transferId;
  }

  public TransferType getTransferType() {
    return transferType;
  }

  public String getOriginService() {
    return originService;
  }

  public String getOriginType() {
    return originType;
  }

  public String getOriginReference() {
    return originReference;
  }

  public ObjectReference getOrderingPartyReference() {
    return orderingPartyReference;
  }

  public Instant getReceivedAt() {
    return receivedAt;
  }

  public UUID getSourceAccountId() {
    return sourceAccountId;
  }

  public UUID getDestinationAccountId() {
    return destinationAccountId;
  }

  public AcceptedDestination getAcceptedDestination() {
    return acceptedDestination;
  }

  public BigDecimal getPrincipalAmount() {
    return principalAmount;
  }

  public Currency getCurrency() {
    return currency;
  }

  public String getPurposeCode() {
    return purposeCode;
  }

  public AuthorizationBasis getAuthorizationBasis() {
    return authorizationBasis;
  }

  public String getAcceptedContentHash() {
    return acceptedContentHash;
  }

  public List<MonetaryConcept> getConcepts() {
    return concepts;
  }

  public ProcessingState getProcessingState() {
    return processingState;
  }

  public boolean isDispatchBarrier() {
    return dispatchBarrier;
  }

  public List<PendingComponent> getPendingComponents() {
    return pendingComponents;
  }

  public List<ConfirmedFact> getConfirmedFacts() {
    return confirmedFacts;
  }

  public List<ObjectReference> getMonetaryEffectReferences() {
    return monetaryEffectReferences;
  }

  public long getVersion() {
    return version;
  }

  public long getLockVersion() {
    return lockVersion;
  }

  public UUID getCreatedActionId() {
    return createdActionId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
