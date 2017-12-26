package com.crypto.deep.marketdatacollector.controller;


import com.crypto.deep.marketdatacollector.service.api.IHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/history")
public class HistoryController {

    private final IHistoryService historyService;

    @Autowired
    public HistoryController(IHistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public ResponseEntity<?> synchronizeAllHistory() {
        historyService.synchronizeHistory();

        return new ResponseEntity<>(OK);
    }
}
