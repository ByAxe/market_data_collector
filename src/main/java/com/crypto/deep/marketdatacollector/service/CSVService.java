package com.crypto.deep.marketdatacollector.service;

import com.crypto.deep.marketdatacollector.core.CSVUtils;
import com.crypto.deep.marketdatacollector.core.Utils;
import com.crypto.deep.marketdatacollector.core.enums.CSV_Type;
import com.crypto.deep.marketdatacollector.model.entity.History;
import com.crypto.deep.marketdatacollector.service.api.ICSVService;
import com.crypto.deep.marketdatacollector.service.api.ICurrencyService;
import com.crypto.deep.marketdatacollector.service.api.IHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.crypto.deep.marketdatacollector.core.Utils.THRESHOLD;

@Service
public class CSVService implements ICSVService {
    private final ICurrencyService currencyService;
    private final IHistoryService historyService;

    @Autowired
    public CSVService(ICurrencyService currencyService, IHistoryService historyService) {
        this.currencyService = currencyService;
        this.historyService = historyService;
    }


    @Override
    public void writeEverythingIntoCsv(CSV_Type type) {
        String fileName = "data_" + type.name().toLowerCase() + ".csv";

        try {
            FileWriter writer = new FileWriter(fileName);

            // Sun Jan 01 2017 11:04:00
            LocalDateTime begin = Utils.convertMillsToLocalDateTime(1483268640000L);
            // Sun Jan 01 2017 11:08:59
            LocalDateTime end = begin.plus(4, ChronoUnit.MINUTES).plus(59, ChronoUnit.SECONDS);

            while (end.isBefore(THRESHOLD)) {
                List<History> historyList = historyService.findAllBetweenDt(begin, end);

                List<String> allNameDistinct = historyList.stream()
                        .map(History::getName)
                        .distinct()
                        .collect(Collectors.toList());

                // Если это биткоин, то уберем из столбцов его самого (иначе, там всегда будет 1.0)
                if (type == CSV_Type.BTC) allNameDistinct.remove("bitcoin");

                // header
                CSVUtils.writeLine(writer, allNameDistinct);


            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
