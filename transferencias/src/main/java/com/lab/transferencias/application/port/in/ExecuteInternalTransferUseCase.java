package com.lab.transferencias.application.port.in;

import com.lab.transferencias.application.command.ExecuteInternalTransferCommand;
import com.lab.transferencias.application.command.RequestContext;
import com.lab.transferencias.application.result.TransferActionResult;

/**
 * Puerto de entrada para ejecutar una transferencia interna.
 * Define el contrato de caso de uso que coordina la validación y la ejecución del movimiento.
 */
public interface ExecuteInternalTransferUseCase {

  TransferActionResult execute(ExecuteInternalTransferCommand command, RequestContext requestContext);

}

