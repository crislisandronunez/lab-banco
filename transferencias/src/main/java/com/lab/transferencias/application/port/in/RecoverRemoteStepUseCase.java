package com.lab.transferencias.application.port.in;

import com.lab.transferencias.application.command.RequestContext;
import com.lab.transferencias.application.result.TransferActionResult;
import java.util.UUID;

/**
 * Puerto de entrada para recuperar un paso remoto pendiente.
 * Define la continuación interna necesaria para resolver una actuación que quedó sin respuesta o con incertidumbre.
 */
public interface RecoverRemoteStepUseCase {

  TransferActionResult recover(UUID transferId, UUID remoteActionId, RequestContext requestContext);
}

