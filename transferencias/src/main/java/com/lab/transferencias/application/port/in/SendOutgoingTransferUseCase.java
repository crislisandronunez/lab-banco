package com.lab.transferencias.application.port.in;

import com.lab.transferencias.application.command.SendOutgoingTransferCommand;
import com.lab.transferencias.application.command.RequestContext;
import com.lab.transferencias.application.result.TransferActionResult;

/**
 * Puerto de entrada para enviar una transferencia externa saliente.
 * Define la operación de coordinación entre la instrucción local y la autoridad de salida.
 */
public interface SendOutgoingTransferUseCase {

  TransferActionResult execute(SendOutgoingTransferCommand command, RequestContext requestContext);
}

