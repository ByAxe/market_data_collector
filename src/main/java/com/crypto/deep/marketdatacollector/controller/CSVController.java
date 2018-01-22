package com.crypto.deep.marketdatacollector.controller;

import com.crypto.deep.marketdatacollector.core.Utils;
import com.crypto.deep.marketdatacollector.core.enums.CSV_Type;
import com.crypto.deep.marketdatacollector.service.api.ICSVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/csv")
public class CSVController {

    private final ICSVService csvService;

    @Autowired
    public CSVController(ICSVService csvService) {
        this.csvService = csvService;
    }

    @PutMapping("/{type}")
    public ResponseEntity<?> updateCurrencies(@PathVariable CSV_Type type,
                                              @RequestParam Long dtBegin, @RequestParam Long dtEnd) {
        LocalDateTime dtBeginAsLDT = Utils.convertMillsToLocalDateTime(dtBegin);
        LocalDateTime dtEndAsLDT = Utils.convertMillsToLocalDateTime(dtEnd);

        csvService.writeEverythingIntoCsv(type, dtBeginAsLDT, dtEndAsLDT);

        return new ResponseEntity<>(OK);
    }

    @PutMapping
    public ResponseEntity<?> updateCurrencies(@RequestParam Long dtBegin, @RequestParam Long dtEnd) {
        LocalDateTime dtBeginAsLDT = Utils.convertMillsToLocalDateTime(dtBegin);
        LocalDateTime dtEndAsLDT = Utils.convertMillsToLocalDateTime(dtEnd);

        Stream.of(CSV_Type.values()).forEach(t -> csvService.writeEverythingIntoCsv(t, dtBeginAsLDT, dtEndAsLDT));

        return new ResponseEntity<>(OK);
    }
}
