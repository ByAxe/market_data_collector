package com.crypto.deep.marketdatacollector.service;

import com.crypto.deep.marketdatacollector.core.Utils;
import com.crypto.deep.marketdatacollector.model.entity.Currency;
import com.crypto.deep.marketdatacollector.repository.api.ICurrencyRepository;
import com.crypto.deep.marketdatacollector.service.api.ICurrencyService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CurrencyService implements ICurrencyService {

    private final ICurrencyRepository currencyRepository;
    private final ObjectMapper objectMapper;
    private final Environment environment;

    @Autowired
    public CurrencyService(ICurrencyRepository currencyRepository, ObjectMapper objectMapper, Environment environment) {
        this.currencyRepository = currencyRepository;
        this.objectMapper = objectMapper;
        this.environment = environment;
    }

    @Override
    public Currency findByName(String name) {
        return currencyRepository.findByName(name);
    }

    @Override
    public List<Currency> findAll() {
        return (List<Currency>) currencyRepository.findAll();
    }

    @Override
    public void save(List<Currency> list) {
        try {
            currencyRepository.save(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Currency currency) {
        currencyRepository.save(currency);
    }

    @Override
    public List<Currency> downloadCurrencies() throws Exception {
        List<Currency> result = new ArrayList<>();

        HttpResponse<String> stringHttpResponse = Unirest.get(environment.getProperty("currency.list.url"))
                .asString();

        List<Map<String, Object>> values = objectMapper.readValue(stringHttpResponse.getBody(), new TypeReference<List<Map<String, Object>>>() {
        });

        for (Map<String, Object> currencyRaw : values) {
            String name = String.valueOf(currencyRaw.get("id"));
            String symbol = String.valueOf(currencyRaw.get("symbol"));
            BigDecimal maxSupply = null;
            Object maxSupplyRaw = currencyRaw.get("max_supply");

            if (maxSupplyRaw != null) maxSupply = Utils.getBigDecimal(maxSupplyRaw);

            result.add(new Currency(name, symbol, maxSupply));
        }


        return result;
    }
}
