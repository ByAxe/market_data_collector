package com.crypto.deep.marketdatacollector.repository;

import com.crypto.deep.marketdatacollector.model.entity.History;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IHistoryRepository extends CrudRepository<History, Long> {

    List<History> findAllByName(String name);

    List<History> findAllByNameAndDtBetween(String name, long begin, long end);

    void deleteAllByName(String name);

    void deleteAllByNameBetween(long begin, long end);

}
