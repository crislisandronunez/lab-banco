package com.lab.transferencias.application.port.in;

import com.lab.transferencias.application.command.ReceiveIncomingTransferCommand;
import com.lab.transferencias.application.result.TransferActionResult;

/**
 * Puerto de entrada para recibir una transferencia externa entrante.
 * Define la operación de aceptación y validación de la instrucción externa del participante.
 */
public interface ReceiveIncomingTransferUseCase {

  TransferActionResult execute(ReceiveIncomingTransferCommand command);
}

