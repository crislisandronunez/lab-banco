package com.lab.transferencias.application.port.out;

import com.lab.transferencias.application.dto.currency.CurrencyExchangeRequest;
import com.lab.transferencias.application.dto.currency.CurrencyExchangeResult;

/**
 * Contrato para verificar una conversión de moneda autorizada por la autoridad competente.
 * La aplicación no calcula tipos de cambio ni decide la conversión por sí misma.
 */
public interface CurrencyExchangePort {

  CurrencyExchangeResult verifyConversion(CurrencyExchangeRequest request);
}
