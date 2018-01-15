package com.crypto.deep.marketdatacollector.controller;

import com.crypto.deep.marketdatacollector.core.enums.CSV_Type;
import com.crypto.deep.marketdatacollector.service.api.ICSVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/csv")
public class CSVController {

    private final ICSVService csvService;

    @Autowired
    public CSVController(ICSVService csvService) {
        this.csvService = csvService;
    }

    @GetMapping("/{type}")
    public ResponseEntity<?> updateCurrencies(@PathVariable CSV_Type type) {
        csvService.writeEverythingIntoCsv(type);

        return new ResponseEntity<>(OK);
    }
}
