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
@Table(name = "history", indexes = {
        @Index(name = "history_pkey", unique = true, columnList = "id"),
        @Index(name = "history_name_key", unique = true, columnList = "name")
})
public class History {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String name;

    @Column
    private Long dt;

    @Column(name = "price_btc")
    private BigDecimal priceBTC;

    @Column(name = "price_usd")
    private BigDecimal priceUSD;

//    public History() {
//    }

    public History(String name, Long dt, BigDecimal priceBTC, BigDecimal priceUSD) {
        this.name = name;
        this.dt = dt;
        this.priceBTC = priceBTC;
        this.priceUSD = priceUSD;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDt() {
        return dt;
    }

    public void setDt(Long dt) {
        this.dt = dt;
    }

    public BigDecimal getPriceBTC() {
        return priceBTC;
    }

    public void setPriceBTC(BigDecimal priceBTC) {
        this.priceBTC = priceBTC;
    }

    public BigDecimal getPriceUSD() {
        return priceUSD;
    }

    public void setPriceUSD(BigDecimal priceUSD) {
        this.priceUSD = priceUSD;
    }

}
