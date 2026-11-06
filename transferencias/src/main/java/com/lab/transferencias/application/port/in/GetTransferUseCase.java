package com.lab.transferencias.application.port.in;

import com.lab.transferencias.application.query.GetTransferQuery;
import com.lab.transferencias.application.result.TransferView;
import com.lab.transferencias.application.command.RequestContext;

/**
 * Puerto de entrada para consultar una transferencia por su identidad principal.
 * Define la operación de lectura autorizada para recuperar el estado y la proyección de la instrucción.
 */
public interface GetTransferUseCase {

  TransferView get(GetTransferQuery query, RequestContext requestContext);
}

