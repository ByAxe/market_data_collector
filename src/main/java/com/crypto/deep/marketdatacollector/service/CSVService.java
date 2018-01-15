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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.crypto.deep.marketdatacollector.core.Utils.*;
import static com.crypto.deep.marketdatacollector.core.enums.CSV_Type.BTC;
import static com.crypto.deep.marketdatacollector.core.enums.CSV_Type.USD;
import static java.time.temporal.ChronoUnit.*;

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
            // Sun Jan 01 2017 11:08:59
            LocalDateTime innerEnd = innerBegin.plus(5, MINUTES).minus(1, MILLIS);

            // Выбираем все называния валют, для которых есть данные с начального периода
            List<String> allNamesDistinct = historyService.findAllBetweenDt(innerBegin, innerEnd)
                    .stream()
                    .map(History::getName)
                    .distinct()
                    .collect(Collectors.toList());

            // Если это биткоин, то уберем из столбцов его самого (иначе, там всегда будет 1.0)
            if (type == BTC) allNamesDistinct.remove("bitcoin");

            // header
            CSVUtils.writeLine(writer, allNamesDistinct);

            // Переменные вынесены за цикл для улучшения производительности
            List<String> resultValuesOrdered;
            Map<String, String> nameAndPrice;
            Set<History> historySet;


            // Внешнее окно выборки из БД
            LocalDateTime outerBegin = LocalDateTime.from(innerBegin);
            LocalDateTime outerEnd = outerBegin.plus(1, MONTHS).minus(1, MILLIS);

            // Внешний цикл выбирает из БД данные за большой промежуток времени
            while (outerBegin.isBefore(THRESHOLD)) {
                System.out.println("Outer window for: " + outerBegin + " - " + outerEnd);
                historySet = historyService.findAllBetweenDt(outerBegin, outerEnd);

                // Внутренний цикл выбирает данные уже из Памяти и режет их на необходимого размера кусочки
                while (innerBegin.isBefore(outerEnd)) {
                    System.out.println("\tInner window for: " + innerBegin + " - " + innerEnd);

                    Long innerBeginAsLong = convertLocalDateTimeToMills(innerBegin);
                    Long innerEndAsLong = convertLocalDateTimeToMills(innerEnd);

                    // Собираем в удобную структуру, где есть только ИМЯ - ЦЕНА
                    // Выбрасываем значения с повторяющимися именами для того чтобы не было ошибки идентичности
                    // при добавлении в Map
                    nameAndPrice = historySet
                            .stream()
                            .filter(h -> h.getDt() >= innerBeginAsLong && h.getDt() < innerEndAsLong)
//                            .sorted(Comparator.comparing(History::getName))
                            .filter(distinctByKey(History::getName))
                            .collect(Collectors.toMap(History::getName,
                                    history -> {
                                        BigDecimal price = history.getPriceBTC();
                                        if (type == USD) price = history.getPriceUSD();
                                        return String.valueOf(price);
                                    }));

                    resultValuesOrdered = new ArrayList<>(allNamesDistinct.size());

                    // Вписываем значения в новый лист в точно таком же порядке, в котором сделаны колонки
                    // И только те валюты, которые уже существовали в самом начале
                    // Иначе, может быть рассинхронизация
                    for (String name : allNamesDistinct) {
                        resultValuesOrdered.add(nameAndPrice.get(name));
                    }

                    // Записываем строку в файл
                    CSVUtils.writeLine(writer, resultValuesOrdered);

                    // Перемещаемся по времени на след. внутреннее окно выборки
                    innerBegin = innerBegin.plus(5, MINUTES);
                    innerEnd = innerEnd.plus(5, MINUTES);
                }

                // Перемещаемся по времени на след. внешнее окно выборки
                outerBegin = outerBegin.plus(1, MONTHS);
                outerEnd = outerEnd.plus(1, MONTHS);

                // Сбрасываем внутреннее окно, до начала внешнего
                innerBegin = LocalDateTime.from(outerBegin);
                innerEnd = innerBegin.plus(5, MINUTES).minus(1, MILLIS);
            }


            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
