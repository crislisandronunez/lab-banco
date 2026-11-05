package com.lab.transferencias.application.port.in;

import com.lab.transferencias.application.querys.SearchTransfersQuery;
import com.lab.transferencias.application.result.TransferSearchResult;

/**
 * Puerto de entrada para buscar transferencias por identidad externa o referencia de negocio.
 * Define la operación de consulta para localizar instrucciones relevantes y paginarlas.
 */
public interface SearchTransfersUseCase {

  TransferSearchResult search(SearchTransfersQuery query);
}

