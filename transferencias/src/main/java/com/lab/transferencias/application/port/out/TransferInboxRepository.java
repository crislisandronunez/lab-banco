package com.lab.transferencias.application.port.out;

import com.lab.transferencias.application.dto.persistence.InboxEventRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato para deduplicar y recuperar mensajes o eventos entrantes sin procesarlos dos veces.
 * La identidad del mensaje se conserva con la del hecho externo que representa.
 */
public interface TransferInboxRepository {

  InboxEventRecord save(InboxEventRecord event);

  Optional<InboxEventRecord> findByMessageId(UUID messageId);

  Optional<InboxEventRecord> findBySourceFact(
      UUID transferId,
      String sourceService,
      UUID sourceFactId,
      long sourceFactVersion);

  List<InboxEventRecord> findByTransferId(UUID transferId);
}
