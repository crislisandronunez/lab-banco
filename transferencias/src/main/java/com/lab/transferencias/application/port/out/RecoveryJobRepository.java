package com.lab.transferencias.application.port.out;

import com.lab.transferencias.application.dto.persistence.RecoveryJobRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato para programar y recuperar trabajos de reconciliación y reintento de pasos remotos pendientes.
 * Permite reanudar la resolución sin perder la identidad original del paso o del efecto.
 */
public interface RecoveryJobRepository {

  RecoveryJobRecord save(RecoveryJobRecord job);

  Optional<RecoveryJobRecord> findById(UUID jobId);

  List<RecoveryJobRecord> findPendingByTransferId(UUID transferId);
}
