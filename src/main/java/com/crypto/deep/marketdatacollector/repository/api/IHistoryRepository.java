package com.crypto.deep.marketdatacollector.repository.api;

import com.crypto.deep.marketdatacollector.model.entity.History;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IHistoryRepository extends CrudRepository<History, Long> {

    List<History> findAllByName(String name);

    List<History> findAllByNameAndDtBetween(String name, long begin, long end);

    @Query(value = "SELECT DISTINCT name FROM market_data.history ", nativeQuery = true)
    List<String> findAllNameDistinct();

    List<History> findAllByDtBetween(long begin, long end);

    void deleteAllByName(String name);

    void deleteAllByDtBetween(long begin, long end);

}
