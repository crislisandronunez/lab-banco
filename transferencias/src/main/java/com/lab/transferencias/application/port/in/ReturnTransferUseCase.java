package com.lab.transferencias.application.port.in;

import com.lab.transferencias.application.command.ReturnTransferCommand;
import com.lab.transferencias.application.command.RequestContext;
import com.lab.transferencias.application.result.TransferActionResult;

/**
 * Puerto de entrada para tramitar una devolución de transferencia.
 * Define la operación de crear una nueva actuación vinculada a la transferencia original.
 */
public interface ReturnTransferUseCase {

  TransferActionResult execute(ReturnTransferCommand command, RequestContext requestContext);
}

