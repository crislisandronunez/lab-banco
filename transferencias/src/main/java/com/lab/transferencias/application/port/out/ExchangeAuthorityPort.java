package com.lab.transferencias.application.port.out;

import com.lab.transferencias.application.dto.exchange.ExchangeCancellationRequest;
import com.lab.transferencias.application.dto.exchange.ExchangeCancellationResult;
import com.lab.transferencias.application.dto.exchange.ExchangeDispatchRequest;
import com.lab.transferencias.application.dto.exchange.ExchangeDispatchResult;
import com.lab.transferencias.application.dto.exchange.ExchangeMechanismRequest;
import com.lab.transferencias.application.dto.exchange.ExchangeMechanismResult;
import com.lab.transferencias.application.dto.exchange.ExchangePreparationRequest;
import com.lab.transferencias.application.dto.exchange.ExchangePreparationResult;
import com.lab.transferencias.application.dto.exchange.ExchangeRecoveryRequest;
import com.lab.transferencias.application.dto.exchange.ExchangeRecoveryResult;
import com.lab.transferencias.application.dto.exchange.ExchangeReturnRequest;
import com.lab.transferencias.application.dto.exchange.ExchangeReturnResult;

/**
 * Contrato para validar, preparar, despachar y recuperar intercambios externos con evidencias separadas.
 * Cada capacidad conserva su identidad, momento y resultado sin mezclar confirmación con preparación.
 */
public interface ExchangeAuthorityPort {

  ExchangeMechanismResult validateMechanism(ExchangeMechanismRequest request);

  ExchangePreparationResult prepareExchange(ExchangePreparationRequest request);

  ExchangeDispatchResult dispatchExchange(ExchangeDispatchRequest request);

  ExchangeCancellationResult requestCancellation(ExchangeCancellationRequest request);

  ExchangeReturnResult requestReturn(ExchangeReturnRequest request);

  ExchangeRecoveryResult recoverExchange(ExchangeRecoveryRequest request);
}

