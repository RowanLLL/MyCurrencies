package com.zucc.lk31501092.mycurrencies;

import org.json.JSONObject;
import org.litepal.crud.LitePalSupport;

public class RateRecord extends LitePalSupport {
    private long timestamp;
    private String rates;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRates() {
        return rates;
    }

    public void setRates(String rates) {
        this.rates = rates;
    }
}
