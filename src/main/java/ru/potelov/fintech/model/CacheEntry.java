package ru.potelov.fintech.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CacheEntry implements Serializable {

    private LocalDate date;
    private BigDecimal rate;
    private String key;

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public String getKey() {
        return key;
    }
}
