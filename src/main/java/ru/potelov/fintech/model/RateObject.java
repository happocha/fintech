package ru.potelov.fintech.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class RateObject implements Serializable {

    private String name;
    private BigDecimal rate;

    public RateObject(String name, BigDecimal rate) {
        this.name = name;
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
