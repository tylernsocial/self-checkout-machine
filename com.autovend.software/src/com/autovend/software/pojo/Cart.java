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

import java.util.ArrayList;

public class Cart {
    private final ArrayList<CartLineItem> cartLineItems;
    private final double taxRate1;
    private final String taxName1;
    private final double taxRate2;
    private final String taxName2;
    private final boolean compoundTaxes;
    private double subtotal;
    private double taxAmount1;
    private double taxAmount2;
    private double total;

    public Cart(String taxName1, double taxRate1, String taxName2, double taxRate2, boolean compoundTaxes) {
        cartLineItems = new ArrayList<>();
        this.taxName1 = taxName1;
        this.taxRate1 = taxRate1;
        this.taxName2 = taxName2;
        this.taxRate2 = taxRate2;
        this.compoundTaxes = compoundTaxes;
        this.subtotal = 0.0;
        this.total = 0.0;
        if (this.taxName1 != null && this.taxName1.trim().length() > 0 && this.taxRate1 <= 0) {
            throw new IllegalArgumentException("Invalid tax rate provided.");
        }
        if (this.taxName2 != null && this.taxName2.trim().length() > 0 && this.taxRate2 <= 0) {
            throw new IllegalArgumentException("Invalid tax rate provided.");
        }
        if (this.taxRate1 == 0 && this.taxRate2 > 0) {
            throw new IllegalArgumentException("Tax Rate2 cannot be provided when Tax Rate1 is 0");
        }
    }

    public String getTaxName1() {
        return taxName1;
    }

    public double getTaxAmount1() {
        return taxAmount1;
    }

    public String getTaxName2() {
        return taxName2;
    }

    public double getTaxAmount2() {
        return taxAmount2;
    }

    public boolean isCompoundTaxes() {
        return compoundTaxes;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getTaxRate1() {
        return taxRate1;
    }

    public double getTaxRate2() {
        return taxRate2;
    }

    public double getTotal() {
        return total;
    }

    public ArrayList<CartLineItem> getCartLineItems() {
        return cartLineItems;
    }

    public boolean addCartItem(CartLineItem lineItem) {
        this.cartLineItems.add(lineItem);
        recalculate();
        return true;
    }

    public boolean removeLineItem(int lineIndex) {
        this.cartLineItems.remove(lineIndex);
        recalculate();
        return true;
    }

    private void recalculate() {
        double linesSubtotal = 0.0;
        double cartTotal = 0.0;
        for (CartLineItem lineItem : this.cartLineItems) {
            linesSubtotal += lineItem.getLineTotalPrice();
        }
        cartTotal = linesSubtotal;
        if (taxRate1 > 0) {
            this.taxAmount1 = cartTotal * this.taxRate1;
        }
        if (taxRate2 > 0) {
            if (compoundTaxes) {
                cartTotal += this.taxAmount1;
            }
            this.taxAmount2 = cartTotal * this.taxRate2;
        }
        cartTotal = linesSubtotal + this.taxAmount1 + this.taxAmount2;
        this.subtotal = linesSubtotal;
        this.total = cartTotal;
    }
}
