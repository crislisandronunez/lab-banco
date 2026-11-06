package com.lab.transferencias.application.port.in;

import com.lab.transferencias.application.command.IncorporateExternalResultCommand;
import com.lab.transferencias.application.command.RequestContext;
import com.lab.transferencias.application.result.TransferActionResult;

/**
 * Puerto de entrada para incorporar un resultado externo tardío o contradictorio.
 * Define la continuidad interna que aplica la evidencia recibida desde otra autoridad o canal.
 */
public interface IncorporateExternalResultUseCase {

  TransferActionResult incorporate(
      IncorporateExternalResultCommand command,
      RequestContext requestContext);
}

