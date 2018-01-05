package com.crypto.deep.marketdatacollector.repository.parsers;

import com.crypto.deep.marketdatacollector.model.Trade;
import com.crypto.deep.marketdatacollector.repository.api.ACsvParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class TradesCsvParserRepository extends ACsvParser<Trade> {

    @Autowired
    public TradesCsvParserRepository(@Qualifier("tClass") Class tClass, ObjectMapper mapper) {
        super(tClass,mapper);
    }



    @Override
    public void writeFile(String fileName) {

    }
}
