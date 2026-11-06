package com.lab.transferencias.application.port.out;

import com.lab.transferencias.application.dto.identity.TransferOriginIdentity;
import com.lab.transferencias.application.dto.identity.TransferExternalIdentity;
import com.lab.transferencias.domain.model.Transfer;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato para guardar y recuperar el agregado Transfer con identidad local y externa.
 * Conserva la instrucción principal y sus componentes críticos sin exponer detalles de infraestructura.
 */
public interface TransferRepository {

  Transfer save(Transfer transfer);

  Optional<Transfer> findById(UUID transferId);

  Optional<Transfer> findByOriginIdentity(TransferOriginIdentity identity);

  Optional<Transfer> findByExternalIdentity(TransferExternalIdentity identity);

  Optional<Transfer> lockForUpdate(UUID transferId);
}
