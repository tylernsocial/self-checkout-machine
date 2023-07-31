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

import com.autovend.*;
import com.autovend.devices.BarcodeScanner;
import com.autovend.devices.ElectronicScale;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.software.controllers.BarcodeScannerController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.ElectronicScaleController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static com.autovend.external.ProductDatabases.BARCODED_PRODUCT_DATABASE;
import static com.autovend.external.ProductDatabases.PLU_PRODUCT_DATABASE;
import static org.junit.Assert.assertEquals;

public class AddItemByTextSearchTest {
    //will test for add when in plu database
    //will test for add when in barcode database
    //will test for when in neither database


    BarcodeScanner stubScanner;
    ElectronicScale stubScale;
    private CheckoutController checkoutController;
    private BarcodeScannerController scannerController;
    private ElectronicScaleController scaleController;
    private BarcodedProduct databaseItem1;
    private PLUCodedProduct databaseItem2;
    private BarcodedUnit validUnit1;
    private PriceLookUpCodedUnit validUnit2;

    /**
     * Setup for testing
     */
    @Before
    public void setup() {
        checkoutController = new CheckoutController();
        scannerController = new BarcodeScannerController(new BarcodeScanner());
        scaleController = new ElectronicScaleController(new ElectronicScale(1000, 1));

        // First item to be scanned
        databaseItem1 = new BarcodedProduct(new Barcode(Numeral.three, Numeral.three, Numeral.five, Numeral.five), "milk",
                BigDecimal.valueOf(83.29), 359.0);

        // Second item to be scanned
        databaseItem2 = new PLUCodedProduct(new PriceLookUpCode(Numeral.four, Numeral.five, Numeral.five, Numeral.five), "rice",
                BigDecimal.valueOf(42));

        validUnit1 = new BarcodedUnit(new Barcode(Numeral.three, Numeral.three, Numeral.five, Numeral.five), 359.0);
        validUnit2 = new PriceLookUpCodedUnit(new PriceLookUpCode(Numeral.four, Numeral.five, Numeral.five, Numeral.five), 42.0);

        BARCODED_PRODUCT_DATABASE.put(databaseItem1.getBarcode(), databaseItem1);
        PLU_PRODUCT_DATABASE.put(databaseItem2.getPLUCode(), databaseItem2);

        stubScanner = new BarcodeScanner();
        stubScale = new ElectronicScale(1000, 1);

        scannerController = new BarcodeScannerController(stubScanner);
        scannerController.setMainController(checkoutController);
        scaleController = new ElectronicScaleController(stubScale);
        scaleController.setMainController(checkoutController);

        stubScanner.register(scannerController);
        stubScale.register(scaleController);

    }

    /**
     * Tears down objects so they can be initialized again with setup
     */
    @After
    public void teardown() {
        checkoutController = null;
        scannerController = null;
        scaleController = null;
        stubScale = null;
        stubScanner = null;

    }

    /**
     * Tests addItem by adding a barcoded Item
     */
    @Test
    public void testAddItemInBarcodeDatabase() {

        // Adds item
        checkoutController.addItemByTextSearch(scannerController, "milk");

        // Adds the cost of the first item to the total
        BigDecimal total = databaseItem1.getPrice();

        // Checks that the item was added and the order was updated to 1
        assertEquals(1, checkoutController.getOrder().size());

        // Checks that the total cost was updated
        assertEquals(total, checkoutController.getCost());
    }

    /**
     * Tests addItem by adding a plu-coded Item
     */
    @Test
    public void testAddItemInPluDatabase() {

        // Adds item
        checkoutController.addItemByTextSearch(scannerController, "rice");

        // Adds the cost of the first item to the total
        BigDecimal total = databaseItem2.getPrice();

        // Checks that the item was added and the order was updated to 1
        assertEquals(1, checkoutController.getOrder().size());

        // Checks that the total cost was updated
        assertEquals(total, checkoutController.getCost());
    }

    /**
     * Tests addItem by attempting to add an item that not in our database
     */
    @Test(expected = NoSuchElementException.class)
    public void testAddItemInNeitherDatabase() {
        // Adds item
        checkoutController.addItemByTextSearch(scannerController, "orange");


    }
}
