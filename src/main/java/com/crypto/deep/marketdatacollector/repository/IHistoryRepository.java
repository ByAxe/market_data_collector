package com.crypto.deep.marketdatacollector.repository;

import com.crypto.deep.marketdatacollector.model.entity.History;
import org.springframework.data.repository.CrudRepository;

public interface IHistoryRepository extends CrudRepository<History, Long> {
}
