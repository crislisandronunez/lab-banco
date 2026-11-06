package com.lab.transferencias.application.port.out;

import com.lab.transferencias.application.dto.persistence.TransferActionSnapshot;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato para conservar y recuperar una actuación bancaria con su resultado original y sus referencias de seguimiento.
 * Permite distinguir la identidad de la petición del efecto económico asociado.
 */
public interface TransferActionRepository {

  TransferActionSnapshot save(TransferActionSnapshot action);

  Optional<TransferActionSnapshot> findByActionId(UUID actionId);

  Optional<TransferActionSnapshot> findByTransferId(UUID transferId);

  Optional<TransferActionSnapshot> findByOperationId(UUID operationId);
}
