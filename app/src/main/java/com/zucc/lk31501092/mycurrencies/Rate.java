package com.zucc.lk31501092.mycurrencies;

import org.litepal.crud.LitePalSupport;

public class Rate extends LitePalSupport {
    private String forCode;
    private String homCode;
    private String amount;

    public String getForCode() {
        return forCode;
    }

    public void setForCode(String forCode) {
        this.forCode = forCode;
    }

    public String getHomCode() {
        return homCode;
    }

    public void setHomCode(String homCode) {
        this.homCode = homCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
