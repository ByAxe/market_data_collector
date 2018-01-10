package com.crypto.deep.marketdatacollector.service;

import com.crypto.deep.marketdatacollector.model.Trade;
import com.crypto.deep.marketdatacollector.repository.api.ICsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class TradesCsvParserService {

    private final ICsvParser<Trade> csvParser;

    @Value("${path.to.trades}")
    private String path;

    @Autowired
    public TradesCsvParserService(ICsvParser<Trade> csvParser) {
        this.csvParser = csvParser;
    }

    //    @PostConstruct
    public void parseTrades() {
        try {
            List<Trade> trades = csvParser.readFile(path + File.separator + "btc" + File.separator + "2017-01.csv");

            System.out.println(trades.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
