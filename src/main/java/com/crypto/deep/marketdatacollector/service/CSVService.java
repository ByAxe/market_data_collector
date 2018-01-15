package com.crypto.deep.marketdatacollector.service;

import com.crypto.deep.marketdatacollector.core.CSVUtils;
import com.crypto.deep.marketdatacollector.core.enums.CSV_Type;
import com.crypto.deep.marketdatacollector.model.entity.History;
import com.crypto.deep.marketdatacollector.service.api.ICSVService;
import com.crypto.deep.marketdatacollector.service.api.ICurrencyService;
import com.crypto.deep.marketdatacollector.service.api.IHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.crypto.deep.marketdatacollector.core.Utils.*;
import static com.crypto.deep.marketdatacollector.core.enums.CSV_Type.BTC;
import static com.crypto.deep.marketdatacollector.core.enums.CSV_Type.USD;

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
            LocalDateTime innerBegin = convertMillsToLocalDateTime(1483268640000L);
//            LocalDateTime begin = convertMillsToLocalDateTime(1483643342000L);
            // Sun Jan 01 2017 11:08:59
            LocalDateTime innerEnd = innerBegin.plus(4, ChronoUnit.MINUTES).plus(59, ChronoUnit.SECONDS);

            Set<History> historyList = historyService.findAllBetweenDt(innerBegin, innerEnd);

            // Выбираем все называния валют, для которых есть данные с начального периода
            List<String> allNamesDistinct = historyList.stream()
                    .map(History::getName)
                    .distinct()
                    .collect(Collectors.toList());

            // Если это биткоин, то уберем из столбцов его самого (иначе, там всегда будет 1.0)
            if (type == BTC) allNamesDistinct.remove("bitcoin");

            // header
            CSVUtils.writeLine(writer, allNamesDistinct);

            List<String> resultValuesOrdered;
            Map<String, String> nameAndPrice;

            LocalDateTime outerBegin = LocalDateTime.from(innerBegin);
            LocalDateTime outerEnd = outerBegin.plus(1, ChronoUnit.MONTHS);

            while (outerBegin.isBefore(THRESHOLD)) {

            }

            while (innerBegin.isBefore(THRESHOLD)) {
                System.out.println("Range for: " + innerBegin + " - " + innerEnd);

                // Собираем в удобную структуру, где есть только ИМЯ - ЦЕНА
                nameAndPrice = historyService.findAllBetweenDt(innerBegin, innerEnd)
                        .stream()
                        .sorted(Comparator.comparing(History::getName))
                        .filter(distinctByKey(History::getName))
                        .collect(Collectors.toMap(History::getName,
                                history -> {
                                    BigDecimal price = history.getPriceBTC();
                                    if (type == USD) price = history.getPriceUSD();
                                    return String.valueOf(price);
                                }));

                resultValuesOrdered = new ArrayList<>(historyList.size());

                // Вписываем значения в новый лист в точно таком же порядке, в котором сделаны колонки
                // Иначе, может быть рассинхронизация
                for (String name : allNamesDistinct) {
                    resultValuesOrdered.add(nameAndPrice.get(name));
                }

                // Записываем строку в файл
                CSVUtils.writeLine(writer, resultValuesOrdered);

                // Перемещаемся по времени на след. промежуток
                innerBegin = innerBegin.plus(5, ChronoUnit.MINUTES);
                innerEnd = innerEnd.plus(5, ChronoUnit.MINUTES);
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
