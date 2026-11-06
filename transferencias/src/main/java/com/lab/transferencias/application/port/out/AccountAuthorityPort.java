package com.lab.transferencias.application.port.out;

import com.lab.transferencias.application.dto.account.AccountValidationRequest;
import com.lab.transferencias.application.dto.account.AccountValidationResult;

/**
 * Contrato para comprobar la cuenta, la moneda y la capacidad operacional de una entidad bancaria.
 * La solicitud y la respuesta llevan la evidencia y el resultado de la autoridad encargada.
 */
public interface AccountAuthorityPort {

  AccountValidationResult validateAccount(AccountValidationRequest request);
}
