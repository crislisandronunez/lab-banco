package com.lab.transferencias.domain.valueobject;

import com.lab.transferencias.domain.exception.DomainInvariantException;

/**
 * Conserva una petición o respuesta remota con un esquema identificado y contenido inmutable.
 */
public record StructuredContent(
    String schemaCode,
    long schemaVersion,
    String contentHash,
    Object data
) {

  public StructuredContent {
    if (schemaCode == null || schemaCode.isBlank()) {
      throw new DomainInvariantException("schemaCode no puede ser nulo ni vacío.");
    }
    if (schemaCode.length() > 80) throw new DomainInvariantException("schemaCode no puede exceder 80 caracteres.");
    if (schemaVersion <= 0) {
      throw new DomainInvariantException("schemaVersion debe ser positivo.");
    }
    if (contentHash == null || !contentHash.matches("^[a-fA-F0-9]{64}$")) {
      throw new DomainInvariantException("contentHash debe ser un SHA-256 hexadecimal de 64 caracteres.");
    }
    if (data == null) {
      throw new DomainInvariantException("data es obligatorio.");
    }
    data = freeze(data);
  }

  private static Object freeze(Object value) {
    if (value == null || value instanceof String || value instanceof Boolean
        || value instanceof Byte || value instanceof Short || value instanceof Integer
        || value instanceof Long || value instanceof java.math.BigDecimal
        || value instanceof java.math.BigInteger || value instanceof java.util.UUID
        || value instanceof java.time.temporal.TemporalAccessor || value instanceof Enum<?>) return value;
    if (value instanceof java.util.Map<?, ?> map) {
      java.util.LinkedHashMap<String, Object> copy = new java.util.LinkedHashMap<>();
      for (var entry : map.entrySet()) {
        if (!(entry.getKey() instanceof String key) || key.isBlank())
          throw new DomainInvariantException("Las claves de data deben ser String no vacíos.");
        if (copy.putIfAbsent(key, freeze(entry.getValue())) != null)
          throw new DomainInvariantException("data contiene claves duplicadas.");
      }
      return java.util.Collections.unmodifiableMap(copy);
    }
    if (value instanceof java.util.List<?> list)
      return list.stream().map(StructuredContent::freeze).toList();
    if (value instanceof java.util.Set<?> set) {
      java.util.LinkedHashSet<Object> copy = new java.util.LinkedHashSet<>();
      for (Object item : set) copy.add(freeze(item));
      return java.util.Collections.unmodifiableSet(copy);
    }
    throw new DomainInvariantException("data debe componerse de valores tipados e inmutables.");
  }
}
