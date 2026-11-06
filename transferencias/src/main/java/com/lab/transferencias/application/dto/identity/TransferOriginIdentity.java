package com.lab.transferencias.application.dto.identity;

/** Identidad empresarial de origen de una instrucción. */
public record TransferOriginIdentity(
    String originService,
    String originType,
    String originReference
) {

  public TransferOriginIdentity {
    if (originService == null || originService.isBlank())
      throw new IllegalArgumentException("originService no puede estar vacío.");
    if (originType == null || originType.isBlank())
      throw new IllegalArgumentException("originType no puede estar vacío.");
    if (originReference == null || originReference.isBlank())
      throw new IllegalArgumentException("originReference no puede estar vacío.");
  }
}
