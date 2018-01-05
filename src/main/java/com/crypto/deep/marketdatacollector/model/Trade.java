package com.crypto.deep.marketdatacollector.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Trade {
    private Date timestamp;
    private BigDecimal price;
    private BigDecimal amount;
    private long id;
}
