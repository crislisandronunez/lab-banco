package com.lab.transferencias.application.port.out;

import com.lab.transferencias.application.dto.persistence.TransferHistoryEntry;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato para conservar la historia de cambios y evidencias de una transferencia en orden cronológico y recuperable.
 * No se eliminan hechos confirmados ni se reemplaza la evidencia por un resumen textual.
 */
public interface TransferHistoryRepository {

  TransferHistoryEntry save(TransferHistoryEntry entry);

  List<TransferHistoryEntry> findByTransferId(UUID transferId);

  Optional<TransferHistoryEntry> findById(UUID historyId);
}
