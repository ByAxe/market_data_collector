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
import java.util.stream.Collectors;

import static com.crypto.deep.marketdatacollector.core.Utils.convertLocalDateTimeToMills;
import static com.crypto.deep.marketdatacollector.core.Utils.distinctByKey;
import static com.crypto.deep.marketdatacollector.core.enums.CSV_Type.USD;
import static java.time.temporal.ChronoUnit.*;

@Service
public class CSVService implements ICSVService {
    //    private static final long HISTORY_BEGINNING = 1483268640000L;
    private static final long HISTORY_BEGINNING = 1506805200000L;
    private static final int INNER_WINDOW_STEP_MINUTES = 6;
    private static final int OUTER_WINDOW_STEP_DAYS = 5;
    private static final int STEP_BACK_FOR_WINDOW_UPPER_BOUND_MILLS = 1;
    private static final String BITCOIN = "bitcoin";
    private final ICurrencyService currencyService;
    private final IHistoryService historyService;

    @Autowired
    public CSVService(ICurrencyService currencyService, IHistoryService historyService) {
        this.currencyService = currencyService;
        this.historyService = historyService;
    }

    @Override
    public void writeEverythingIntoCsv(CSV_Type type, LocalDateTime lowerBound, LocalDateTime upperBound) {
        String fileName = "data_" + type.name().toLowerCase() + ".csv";

//        String pathToResources = "src" + File.separator
//                + "main" + File.separator
//                + "resources" + File.separator
//                + "data" + File.separator;

        try {
            FileWriter writer = new FileWriter(fileName);

            // Sun Jan 01 2017 11:04:00
            LocalDateTime innerBegin = lowerBound;
            // Sun Jan 01 2017 11:08:59
            LocalDateTime innerEnd = innerBegin.plus(INNER_WINDOW_STEP_MINUTES, MINUTES).minus(STEP_BACK_FOR_WINDOW_UPPER_BOUND_MILLS, MILLIS);

            // Выбираем все называния валют, для которых есть данные с начального периода
            List<String> allNamesDistinct = historyService.findAllBetweenDt(innerBegin, innerEnd)
                    .stream()
                    .map(History::getName)
                    .distinct()
                    .collect(Collectors.toList());

            // Если это биткоин, то уберем из столбцов его самого (иначе, там всегда будет 1.0)
//            if (type == BTC) allNamesDistinct.remove(BITCOIN);

            // header
            CSVUtils.writeLine(writer, allNamesDistinct);

            // Переменные вынесены за цикл для улучшения производительности
            List<String> resultValuesOrdered;
            Map<String, String> nameAndPrice;
            List<History> historyList;

            // Внешнее окно выборки из БД
            LocalDateTime outerBegin = LocalDateTime.from(innerBegin);
            LocalDateTime outerEnd = outerBegin.plus(OUTER_WINDOW_STEP_DAYS, DAYS).minus(STEP_BACK_FOR_WINDOW_UPPER_BOUND_MILLS, MILLIS);

            // Внешний цикл выбирает из БД данные за большой промежуток времени
            while (outerBegin.isBefore(upperBound)) {
                if (outerEnd.isAfter(upperBound)) outerEnd = upperBound;
                System.out.println("Outer window for: " + outerBegin + " - " + outerEnd);
                historyList = historyService.findAllBetweenDt(outerBegin, outerEnd);
                System.out.println("Outer window size: " + historyList.size() + "\n");

                // Внутренний цикл выбирает данные уже из Памяти и режет их на необходимого размера кусочки
                while (innerBegin.isBefore(outerEnd)) {
//                    System.out.println("\tInner window for: " + innerBegin + " - " + innerEnd);

                    Long innerBeginAsLong = convertLocalDateTimeToMills(innerBegin);
                    Long innerEndAsLong = convertLocalDateTimeToMills(innerEnd);

                    /*
                        Собираем в удобную структуру, где есть только ИМЯ - ЦЕНА
                        Выбрасываем значения с повторяющимися именами для того,
                        чтобы не было ошибки идентичности при добавлении в Map
                    */
                    nameAndPrice = historyList
                            .stream()
                            .filter(h -> h.getDt() >= innerBeginAsLong && h.getDt() < innerEndAsLong)
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
                    innerBegin = innerBegin.plus(INNER_WINDOW_STEP_MINUTES, MINUTES);
                    innerEnd = innerEnd.plus(INNER_WINDOW_STEP_MINUTES, MINUTES);
                }

                // Перемещаемся по времени на след. внешнее окно выборки
                outerBegin = outerBegin.plus(OUTER_WINDOW_STEP_DAYS, DAYS);
                outerEnd = outerEnd.plus(OUTER_WINDOW_STEP_DAYS, DAYS);

                // Сбрасываем внутреннее окно, до начала внешнего
                innerBegin = LocalDateTime.from(outerBegin);
                innerEnd = innerBegin.plus(INNER_WINDOW_STEP_MINUTES, MINUTES).minus(STEP_BACK_FOR_WINDOW_UPPER_BOUND_MILLS, MILLIS);
            }

            writer.flush();
            writer.close();
            System.out.println("Finished successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
