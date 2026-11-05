package com.lab.transferencias.application.port.in;

import com.lab.transferencias.application.querys.GetTransferActionQuery;
import com.lab.transferencias.application.result.TransferActionView;

/**
 * Puerto de entrada para consultar el detalle de una actuación persistente.
 * Define la operación de lectura para recuperar la petición original y su resultado asociado.
 */
public interface GetTransferActionUseCase {

  TransferActionView get(GetTransferActionQuery query);
}

