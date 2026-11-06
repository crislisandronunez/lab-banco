package com.lab.transferencias.application.dto.persistence;

/**
 * Identidad de origen de una instrucción de transferencia para su reconocimiento local y deduplicación.
 */
public record TransferOriginIdentity(
    String originService,
    String originType,
    String originReference
) {
}

