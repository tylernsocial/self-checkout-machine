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

package com.autovend.software.test;

import com.autovend.Barcode;
import com.autovend.Bill;
import com.autovend.Numeral;
import com.autovend.devices.BillDispenser;
import com.autovend.devices.OverloadException;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.BillDispenserController;
import com.autovend.software.controllers.BillPaymentController;
import com.autovend.software.controllers.CheckoutController;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class BillDispenserControllerTest {
    SelfCheckoutStation selfCheckoutStation;
    CheckoutController checkoutControllerStub;
    BillPaymentController billPaymentControllerStub;
    int[] billDenominations;
    BigDecimal[] coinDenominations;
    LinkedHashMap<Product, Number[]> order;

    @Before
    public void setup() {
        // Init denominations
        billDenominations = new int[]{5, 10, 20, 50, 100};
        coinDenominations = new BigDecimal[]{new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal(100), new BigDecimal(200)};

        selfCheckoutStation = new SelfCheckoutStation(Currency.getInstance("CAD"), billDenominations, coinDenominations, 200, 1);

        checkoutControllerStub = new CheckoutController();
        billPaymentControllerStub = new BillPaymentController(selfCheckoutStation.billValidator);
        billPaymentControllerStub.setMainController(checkoutControllerStub);
        checkoutControllerStub.registerPaymentController(billPaymentControllerStub);

        BarcodedProduct barcodedProduct;
        barcodedProduct = new BarcodedProduct(new Barcode(Numeral.one, Numeral.one), "test item 1",
                BigDecimal.valueOf(83.29), 400.0);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodedProduct.getBarcode(), barcodedProduct);
        barcodedProduct = new BarcodedProduct(new Barcode(Numeral.one, Numeral.two), "test item 2",
                BigDecimal.valueOf(50.00), 359.00);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodedProduct.getBarcode(), barcodedProduct);
        barcodedProduct = new BarcodedProduct(new Barcode(Numeral.one, Numeral.three), "test item 3",
                BigDecimal.valueOf(29.99), 125.25);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodedProduct.getBarcode(), barcodedProduct);
        barcodedProduct = new BarcodedProduct(new Barcode(Numeral.one, Numeral.four), "test item 4",
                BigDecimal.valueOf(9.95), 26.75);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodedProduct.getBarcode(), barcodedProduct);

        int billCountToAdd = 100;
        for (Map.Entry<Integer, BillDispenser> entry : selfCheckoutStation.billDispensers.entrySet()) {
            int value = entry.getKey();
            try {
                for (int i = 0; i < billCountToAdd; i++) {
                    entry.getValue().load(new Bill(value, Currency.getInstance("CAD")));
                }
            } catch (OverloadException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void partialPaymentAndRemainingAmountTest() {
        // create order cart
        BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.three));
        order = new LinkedHashMap<>();
        order.put(product, new Number[1]);

        checkoutControllerStub.setOrder(order);

        try {
            selfCheckoutStation.billInput.accept(new Bill(10, Currency.getInstance("CAD")));
        } catch (Exception ex) {
            System.out.printf("Exception " + ex.getMessage());
        }

        double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
        double expextedAmount = new BigDecimal("19.99").doubleValue();

        assertEquals(amountRemaining, expextedAmount, 0);
    }

    @Test
    public void partialPaymentChangeAndRemainingAmountTest() {
        // create order cart
        BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.four));
        order = new LinkedHashMap<>();
        order.put(product, new Number[1]);

        checkoutControllerStub.setOrder(order);

        try {
            selfCheckoutStation.billInput.accept(new Bill(20, Currency.getInstance("CAD")));
        } catch (Exception ex) {
            System.out.printf("Exception " + ex.getMessage());
        }

        BillDispenserController billDispenserController = new BillDispenserController(selfCheckoutStation.billDispensers.get(10), new BigDecimal(10));
        billDispenserController.emitChange();
        selfCheckoutStation.billOutput.removeDanglingBill();
        double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
        double expextedAmount = BigDecimal.valueOf(-0.05).doubleValue();

        assertNotEquals(amountRemaining, expextedAmount);
    }

    @Test
    public void emptyBillDispenserTest() {
        // create order cart
        BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.four));
        order = new LinkedHashMap<>();
        order.put(product, new Number[1]);

        checkoutControllerStub.setOrder(order);

        BillDispenserController billDispenserController = new BillDispenserController(selfCheckoutStation.billDispensers.get(10), new BigDecimal(10));
        billDispenserController.setMainController(checkoutControllerStub);
        for (int i = 0; i < 100; i++) {
            billDispenserController.emitChange();
            selfCheckoutStation.billOutput.removeDanglingBill();
        }
        assertEquals(selfCheckoutStation.billDispensers.get(10).size(), 0);
    }

    @Test(expected = OverloadException.class)
    public void overfillBillDispenserTest() throws OverloadException {
        for (Map.Entry<Integer, BillDispenser> entry : selfCheckoutStation.billDispensers.entrySet()) {
            int value = entry.getKey();
            for (int i = 0; i < 2; i++) {
                entry.getValue().load(new Bill(value, Currency.getInstance("CAD")));
            }
        }
    }
}
