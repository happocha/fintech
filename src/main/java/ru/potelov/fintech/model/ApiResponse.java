package ru.potelov.fintech.model;

import java.io.Serializable;

public class ApiResponse implements Serializable {

    private String base;
    private String date;
    private RateObject rates;

    public String getBase() {
        return base;
    }

    public RateObject getRates() {
        return rates;
    }

    public String getDate() {
        return date;
    }
}
