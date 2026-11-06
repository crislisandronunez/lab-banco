package com.lab.transferencias.application.port.out;

import com.lab.transferencias.application.dto.persistence.IdempotencyRecord;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato para conservar la clave de idempotencia, la huella del contenido y la actuación original vinculada.
 * La comparación de contenido y conflicto se resuelven en el servicio de aplicación, no aquí.
 */
public interface IdempotencyRepository {

  IdempotencyRecord save(IdempotencyRecord record);

  Optional<IdempotencyRecord> findById(UUID idempotencyId);

  Optional<IdempotencyRecord> findByScopeAndRequestKey(String scope, String requestKey);

  Optional<IdempotencyRecord> findByActionId(UUID actionId);
}
