package com.crypto.deep.marketdatacollector.service.api;

import com.crypto.deep.marketdatacollector.model.entity.Currency;

import java.util.List;

public interface ICurrencyService {

    Currency findByName(String name);

    List<Currency> findAll();

    void save(List<Currency> list);

    void save(Currency currency);

    List<Currency> downloadCurrencies() throws Exception;

}
