package com.crypto.deep.marketdatacollector.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "currencies", indexes = {
        @Index(name = "currencies_pkey", unique = true, columnList = "id"),
        @Index(name = "currencies_name_key", unique = true, columnList = "name")
})
public class Currency {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String symbol;

    @Column(name = "max_supply")
    private BigDecimal maxSupply;

    @Column(columnDefinition = "TEXT")
    private String description;

    public Currency() {
    }

    public Currency(String name, String symbol, BigDecimal maxSupply) {
        this.name = name;
        this.symbol = symbol;
        this.maxSupply = maxSupply;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
