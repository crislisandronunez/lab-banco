package com.lab.transferencias.application.port.out;

import com.lab.transferencias.application.dto.persistence.OutboxEventRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato para guardar eventos de salida una vez confirmados localmente y entregarlos de forma durable.
 * La identificación del evento y la del hecho empresarial se conservan por separado.
 */
public interface TransferOutboxRepository {

  OutboxEventRecord save(OutboxEventRecord event);

  List<OutboxEventRecord> findPendingByTransferId(UUID transferId);

  List<OutboxEventRecord> findPending(int limit);

  Optional<OutboxEventRecord> findById(UUID eventId);
}
