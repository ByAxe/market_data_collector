package com.crypto.deep.marketdatacollector.repository.api;

import com.crypto.deep.marketdatacollector.model.entity.Currency;
import org.springframework.data.repository.CrudRepository;

public interface ICurrencyRepository extends CrudRepository<Currency, Long> {

    Currency findByName(String name);
}
