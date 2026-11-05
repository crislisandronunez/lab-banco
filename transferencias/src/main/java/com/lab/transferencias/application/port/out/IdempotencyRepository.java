package com.lab.transferencias.application.port.out;

/**
 * Contrato para controlar la idempotencia de una actuación o instrucción empresarial.
 * Evita repetir efectos cuando llega la misma operación con la misma identidad.
 */
public interface IdempotencyRepository {
}

