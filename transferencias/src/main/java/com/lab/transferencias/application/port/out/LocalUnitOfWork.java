package com.lab.transferencias.application.port.out;

import java.util.concurrent.Callable;

/**
 * Contrato para ejecutar una operación tipada dentro de una unidad transaccional local y confirmar o revertir sus cambios.
 * Debe permanecer neutro frente a Spring, JPA o PostgreSQL y dar soporte a cambios atómicos de la base propia.
 */
public interface LocalUnitOfWork {

  <T> T executeInTransaction(Callable<T> work) throws Exception;

  void executeInTransaction(Runnable work) throws Exception;
}