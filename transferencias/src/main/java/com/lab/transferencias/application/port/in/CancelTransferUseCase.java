package com.lab.transferencias.application.port.in;

import com.lab.transferencias.application.command.CancelTransferCommand;
import com.lab.transferencias.application.result.TransferActionResult;

/**
 * Puerto de entrada para cancelar una transferencia.
 * Define la acción de detener una instrucción cuando la operación aún no ha quedado firme.
 */
public interface CancelTransferUseCase {

  TransferActionResult execute(CancelTransferCommand command);
}

