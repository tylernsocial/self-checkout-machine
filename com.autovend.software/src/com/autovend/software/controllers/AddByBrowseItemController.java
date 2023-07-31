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
package com.autovend.software.controllers;

import com.autovend.BarcodedUnit;
import com.autovend.devices.SimulationException;
import com.autovend.devices.TouchScreen;
import com.autovend.devices.observers.TouchScreenObserver;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class AddByBrowseItemController extends ItemAdderController<TouchScreen, TouchScreenObserver> implements TouchScreenObserver {

    List<BarcodedProduct> browsedItem = new ArrayList<BarcodedProduct>();
    BarcodedUnit barcodedUnit;
    BigDecimal itemPrice;
    BarcodedProduct barcodedItem;
    double itemWeight;

    public AddByBrowseItemController(TouchScreen screen) {
        super(screen);
        // TODO Auto-generated constructor stub
    }

    /*
     * get browsed Item
     * parameter(item name, quantity of items)
     *
     */
    public void SelecetedProduct(String itemName) {
        for (BarcodedProduct product : ProductDatabases.BARCODED_PRODUCT_DATABASE.values()) {
            //if product exist in database
            if (product.getDescription().equals(itemName)) {
                barcodedItem = product;
                itemPrice = product.getPrice();    //price of item
                itemWeight = product.getExpectedWeight(); //item weight
            }
        }
    }


    /*
     * Event When item is added by browsing
     */
    public void AddByBrowsingEvent(String selectedItem) {
        String item = selectedItem;
        SelecetedProduct(item);

        //if inputed item is not null
        if (barcodedItem != null) {
            //add item to order
            this.getMainController().addItem(this, barcodedItem, itemWeight);

        }
        //if item added does not exit through exception
        else {
            throw new SimulationException("Product does not exist");
        }

    }


}
