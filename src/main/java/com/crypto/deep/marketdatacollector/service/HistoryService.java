package com.crypto.deep.marketdatacollector.service;

import com.crypto.deep.marketdatacollector.core.Utils;
import com.crypto.deep.marketdatacollector.model.entity.Currency;
import com.crypto.deep.marketdatacollector.model.entity.History;
import com.crypto.deep.marketdatacollector.repository.api.IHistoryRepository;
import com.crypto.deep.marketdatacollector.service.api.ICurrencyService;
import com.crypto.deep.marketdatacollector.service.api.IHistoryService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.crypto.deep.marketdatacollector.core.Utils.THRESHOLD;

@Service
public class HistoryService implements IHistoryService {

    private final IHistoryRepository historyRepository;
    private final ICurrencyService currencyService;
    private final ObjectMapper objectMapper;
    private final Environment environment;

    @Autowired
    public HistoryService(IHistoryRepository historyRepository, ICurrencyService currencyService, ObjectMapper objectMapper, Environment environment) {
        this.historyRepository = historyRepository;
        this.currencyService = currencyService;
        this.objectMapper = objectMapper;
        this.environment = environment;
    }

    @Override
    public void save(List<History> list) {
        System.out.println(new Date().toString() + ": " + list.size());
        historyRepository.save(list);
    }

    @Override
    public void save(History history) {
        historyRepository.save(history);
    }

    @Override
    public List<History> findByName(String name) {
        return historyRepository.findAllByName(name);
    }

    @Override
    public List<History> findByName(String name, LocalDateTime begin, LocalDateTime end) {
        long b = Utils.convertLocalDateTimeToMills(begin);
        long e = Utils.convertLocalDateTimeToMills(end);

        return historyRepository.findAllByNameAndDtBetween(name, b, e);
    }

    @Override
    public List<History> findAll() {
        return (List<History>) historyRepository.findAll();
    }

    @Override
    public List<String> findAllNameDistinct() {
        return historyRepository.findAllNameDistinct();
    }

    @Override
    public Set<History> findAllBetweenDt(LocalDateTime begin, LocalDateTime end) {
        long beginInMills = Utils.convertLocalDateTimeToMills(begin);
        long endInMills = Utils.convertLocalDateTimeToMills(end);

        return historyRepository.findAllByDtBetween(beginInMills, endInMills);
    }

    @Override
    public List<History> synchronizeHistory(String name, LocalDateTime begin, LocalDateTime end) throws Exception {
        List<History> result = new ArrayList<>();

        long beginInMills = Utils.convertLocalDateTimeToMills(begin);
        long endInMills = Utils.convertLocalDateTimeToMills(end);

        String url = environment.getProperty("history.data.url") + name + "/" + beginInMills + "/" + endInMills;

        Random r = new Random();
        int low = 1200;
        int high = 2700;
        Thread.sleep(r.nextInt(high - low) + low);

        HttpResponse<String> stringHttpResponse = Unirest.get(url)
                .asString();

        Map<String, List<List<Object>>> values = objectMapper.readValue(stringHttpResponse.getBody(), new TypeReference<Map<String, List<List<BigDecimal>>>>() {
        });

        List<List<Object>> priceBtcList = values.get("price_btc");
        List<List<Object>> priceUsdList = values.get("price_usd");

        int size = priceBtcList.size();

        for (int i = 0; i < size; i++) {
            List<Object> priceBtcPair = priceBtcList.get(i);
            List<Object> priceUsdPair = priceUsdList.get(i);

            Long dt = Long.valueOf(String.valueOf(priceBtcPair.get(0)));
            BigDecimal priceBTC = Utils.getBigDecimal(priceBtcPair.get(1));
            BigDecimal priceUSD = Utils.getBigDecimal(priceUsdPair.get(1));

            result.add(new History(name, dt, priceBTC, priceUSD));
        }

        return result;
    }

    @Override
    public void synchronizeHistory() {
        List<Currency> currencies = currencyService.findAll();

        List<String> alreadyPresent = findAllNameDistinct();

        currencies.stream()
                .map(Currency::getName)
                .filter(o -> !alreadyPresent.contains(o))
                .forEach(name -> {
                    System.out.println(new Date().toString() + ": " + name);

                    LocalDateTime beginDate = LocalDateTime.of(2017, 1, 1, 14, 0, 0);
                    LocalDateTime endDate = LocalDateTime.of(2017, 1, 2, 13, 59, 59);

                    try {
                        while (beginDate.isBefore(THRESHOLD)) {
                            save(synchronizeHistory(name, beginDate, endDate));

                            beginDate = beginDate.plusDays(1);
                            endDate = endDate.plusDays(1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
    }


    @Override
    public void deleteAllBetween(LocalDateTime begin, LocalDateTime end) {
        long beginInMills = Utils.convertLocalDateTimeToMills(begin);
        long endInMills = Utils.convertLocalDateTimeToMills(end);

        historyRepository.deleteAllByDtBetween(beginInMills, endInMills);
    }

    @Override
    public void deleteAllForCurrency(String name) {
        historyRepository.deleteAllByName(name);
    }

    @Override
    public void deleteAll() {
        historyRepository.deleteAll();
    }
}
