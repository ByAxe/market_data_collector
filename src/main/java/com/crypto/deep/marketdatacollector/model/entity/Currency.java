package com.crypto.deep.marketdatacollector.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public Currency(String name, String symbol, BigDecimal maxSupply) {
        this.name = name;
        this.symbol = symbol;
        this.maxSupply = maxSupply;
    }

    public String getName() {
        return name.replaceAll(" ", "");
    }

    public String getSymbol() {
        return symbol.replaceAll(" ", "");
    }
}
