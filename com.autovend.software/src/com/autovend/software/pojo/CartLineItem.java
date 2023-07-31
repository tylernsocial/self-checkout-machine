/*
SENG 300 Project Iteration 3
Group 8

UCID		Name
10101894	Nicholas Flanagan-Bergeron
30018276	Shijia(David) Wang
30066080	Xinzhou Li
30072318	Haoping Zheng
30106232	Aman Hossain
30113185	Mingyang Li
30116450	Jitaksha Batish
30116484	Rahat Chowdhury
30118846	Aneel Parmar
30127597	Nam Anh Vu
30130139	Alan Alcocer-Iturriza
30139344	Youssef Samaan
30140581	Eyram Ekpe
30141134	David Oti-George
30141208	Tamerlan Ormanbayev
30141335	Janet Tsegazeab
30146181	Efren Garcia
30148021	Haziq Khawaja
30148838	Ryan Janiszewski
30150496	Alireza Vafisani
30150892	Abrar Zawad Safwan
30151170	Jordan Tewnion
30157743	Aishan Irfan
30158563	Tyler Nguyen
30159927	Aaron Tigley

*/

package com.autovend.software.pojo;

import java.math.BigDecimal;

public class CartLineItem {
    private final boolean isPerUnit;
    private final String description;
    private final double expectedWeight;
    private String productCode;
    private CODETYPE codeType;
    private BigDecimal price;
    private double quantity;
    private double lineTotalPrice;

    public CartLineItem(String productCode, CODETYPE codeType, BigDecimal price, boolean isPerUnit, String description,
                        double expectedWeight, double quantity) {
        this.productCode = productCode;
        this.codeType = codeType;
        this.price = price;
        this.isPerUnit = isPerUnit;
        this.description = description;
        this.expectedWeight = expectedWeight;
        this.quantity = quantity;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public CODETYPE getCodeType() {
        return codeType;
    }

    public void setCodeType(CODETYPE codeType) {
        this.codeType = codeType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        recalculate();
    }

    public boolean isPerUnit() {
        return isPerUnit;
    }

    public String getDescription() {
        return description;
    }

    public double getExpectedWeight() {
        return expectedWeight;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
        recalculate();
    }

    public double getLineTotalPrice() {
        return lineTotalPrice;
    }

    private void recalculate() {
        this.lineTotalPrice = this.price.doubleValue() * quantity;
    }

    public enum CODETYPE {
        BARCODE, PLU
    }
}
