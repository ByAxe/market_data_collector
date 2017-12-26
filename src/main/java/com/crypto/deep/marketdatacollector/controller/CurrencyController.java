package com.crypto.deep.marketdatacollector.controller;

import com.crypto.deep.marketdatacollector.model.entity.Currency;
import com.crypto.deep.marketdatacollector.service.api.ICurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/currency")
public class CurrencyController {
    private final ICurrencyService currencyService;

    @Autowired
    public CurrencyController(ICurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping
    public ResponseEntity<?> updateCurrencies() throws Exception {
        List<Currency> currencies = currencyService.downloadCurrencies();

        currencyService.save(currencies);

        return new ResponseEntity<>(OK);
    }
}
