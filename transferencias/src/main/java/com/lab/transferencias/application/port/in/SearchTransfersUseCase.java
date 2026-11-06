package com.lab.transferencias.application.port.in;

import com.lab.transferencias.application.query.SearchTransfersQuery;
import com.lab.transferencias.application.result.TransferView;
import com.lab.transferencias.application.command.RequestContext;
import java.util.Optional;

/**
 * Puerto de entrada para buscar transferencias por identidad externa o referencia de negocio.
 * Define la operación de consulta para localizar instrucciones relevantes y paginarlas.
 */
public interface SearchTransfersUseCase {

  Optional<TransferView> search(SearchTransfersQuery query, RequestContext requestContext);
}

