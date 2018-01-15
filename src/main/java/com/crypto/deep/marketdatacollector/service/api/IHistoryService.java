package com.crypto.deep.marketdatacollector.service.api;

import com.crypto.deep.marketdatacollector.model.entity.History;

import java.time.LocalDateTime;
import java.util.List;

public interface IHistoryService {
    void save(List<History> list);

    void save(History history);

    List<History> findByName(String name);

    List<History> findByName(String name, LocalDateTime begin, LocalDateTime end);

    List<History> findAll();

    List<String> findAllNameDistinct();

    List<History> findAllBetweenDt(LocalDateTime begin, LocalDateTime end);

    List<History> synchronizeHistory(String name, LocalDateTime begin, LocalDateTime end) throws Exception;

    void synchronizeHistory();

    void deleteAllBetween(LocalDateTime begin, LocalDateTime end);

    void deleteAllForCurrency(String name);

    void deleteAll();
}
