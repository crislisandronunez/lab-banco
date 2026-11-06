package com.lab.transferencias.application.port.out;

import com.lab.transferencias.application.dto.funds.FundsExecutionRequest;
import com.lab.transferencias.application.dto.funds.FundsExecutionResult;
import com.lab.transferencias.application.dto.funds.FundsIncomingCreditRequest;
import com.lab.transferencias.application.dto.funds.FundsMonetaryEffectResult;
import com.lab.transferencias.application.dto.funds.FundsRecoveryRequest;
import com.lab.transferencias.application.dto.funds.FundsRecoveryResult;
import com.lab.transferencias.application.dto.funds.FundsReservationConsumptionRequest;
import com.lab.transferencias.application.dto.funds.FundsReservationProtectionRequest;
import com.lab.transferencias.application.dto.funds.FundsReservationQuery;
import com.lab.transferencias.application.dto.funds.FundsReservationReleaseRequest;
import com.lab.transferencias.application.dto.funds.FundsReservationRequest;
import com.lab.transferencias.application.dto.funds.FundsReservationResult;
import com.lab.transferencias.application.dto.funds.FundsReturnEffectRequest;

/**
 * Contrato para ejecutar efectos monetarios y consultas de reserva con identidad estable.
 * Cada operación se expresa con su propio request/result para no mezclar confirmación, reserva y recuperación.
 */
public interface FundsAuthorityPort {

  FundsExecutionResult executeInternalMovements(FundsExecutionRequest request);

  FundsReservationResult createReservation(FundsReservationRequest request);

  FundsReservationResult protectReservation(FundsReservationProtectionRequest request);

  FundsMonetaryEffectResult consumeReservation(FundsReservationConsumptionRequest request);

  FundsReservationResult releaseReservation(FundsReservationReleaseRequest request);

  FundsMonetaryEffectResult creditIncoming(FundsIncomingCreditRequest request);

  FundsMonetaryEffectResult executeReturnEffect(FundsReturnEffectRequest request);

  FundsRecoveryResult recoverMonetaryAction(FundsRecoveryRequest request);

  FundsReservationResult getReservation(FundsReservationQuery request);
}
