package com.lab.transferencias.application.port.in;

import com.lab.transferencias.application.command.RequestContext;
import com.lab.transferencias.application.result.TransferActionResult;
import java.util.UUID;

/** Puerto interno para reanudar trabajo durable de una transferencia. */
public interface ResumeTransferWorkUseCase {

  TransferActionResult resume(UUID transferId, RequestContext requestContext);
}
