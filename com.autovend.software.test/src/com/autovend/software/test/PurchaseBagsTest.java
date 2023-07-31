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
import com.autovend.BarcodedUnit;
import com.autovend.Numeral;
import com.autovend.devices.BarcodeScanner;
import com.autovend.devices.ElectronicScale;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.BarcodeScannerController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.ElectronicScaleController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class PurchaseBagsTest {

    BarcodeScannerController scannerController;
    BarcodeScanner stubScanner;
    ElectronicScaleController scaleController;
    ElectronicScale stubScale;
    BarcodedProduct newBag;

    BarcodedUnit validUnit;
    CheckoutController checkoutController;

    private Scanner scan;

    @Before
    public void setup() {
        newBag = new BarcodedProduct(new Barcode(Numeral.one, Numeral.zero), "new bag", BigDecimal.valueOf(2.50),
                500.0);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(newBag.getBarcode(), newBag);
        checkoutController = new CheckoutController();

        validUnit = new BarcodedUnit(new Barcode(Numeral.three, Numeral.three), 500.0);

        stubScanner = new BarcodeScanner();
        stubScale = new ElectronicScale(1000, 1);

        scannerController = new BarcodeScannerController(stubScanner);
        scannerController.setMainController(checkoutController);

        scaleController = new ElectronicScaleController(stubScale);
        scaleController.setMainController(checkoutController);

        scan = new Scanner(System.in);
    }

    /**
     * Tears down objects so they can be initialized again with setup
     */
    @After
    public void teardown() {

        checkoutController = null;
        stubScanner = null;
        scannerController = null;
        scaleController = null;
        stubScale = null;
        scan.close();
    }

    /**
     * Test to check the method with 0 bags
     */
    @Test
    public void testGetBagNumber_zeroBags() {
        String input = "0";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));
        assertEquals(0, checkoutController.getBagNumber());
    }

    /**
     * Test to check the method with valid inputs
     */
    @Test
    public void testGetBagNumber_validInput() {
        String input = "5";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));
        assertEquals(5, checkoutController.getBagNumber());
    }

    /**
     * Test to check the method with invalid inputs
     */
    @Test
    public void testGetBagNumber_invalidInput() {
        String input = "five";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));
        assertThrows(NumberFormatException.class, () -> checkoutController.getBagNumber());
    }

    /**
     * Test to check the method with empty input
     */
    @Test
    public void testGetBagNumber_emptyInput() {
        String input = "";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));
        assertThrows(NoSuchElementException.class, () -> checkoutController.getBagNumber());
    }

    /**
     * Test to check if the method returns if the number of bags is 0
     */
    @Test
    public void testPurchaseBags_0Bags() {

        checkoutController.purchaseBags(scannerController, newBag, validUnit.getWeight(), 0);

        // Unblocks the station and lets a new item be scanned
        checkoutController.baggedItemsValid(scaleController);

        assertEquals(BigDecimal.ZERO, checkoutController.getCost());
    }

    /**
     * Test to check if the bags get added to the order correctly and the cost is
     * calculated correctly
     */
    @Test
    public void testPurchaseBags_checkOrder() {

        // Set the number of bags to 3 bags;
        int numBags = 3;

        // create the variable to calculate the cost of the bags in total
        BigDecimal expectedPrice = BigDecimal.ZERO;
        HashMap<Product, Number[]> order = checkoutController.getOrder();

        // Add the bag to the order
        checkoutController.purchaseBags(scannerController, newBag, validUnit.getWeight(), numBags);
        expectedPrice = expectedPrice.add(newBag.getPrice().multiply(BigDecimal.valueOf(numBags)));

        // Unblocks the station and lets a new item be scanned
        checkoutController.baggedItemsValid(scaleController);

        // Check that the bag number and cost in the order were updated correctly
        assertEquals(numBags, order.get(newBag)[0]);
        assertEquals(expectedPrice, checkoutController.getCost());

    }

    /**
     * Test to check if the bags get added correctly after the bag addition prompt is used multiple times
     * Also checks if the cost is getting calculated correctly
     */
    @Test
    public void testPurchaseBags_multipleBags() {

        // create the variable to calculate the cost of the bags in total
        BigDecimal expectedPrice = BigDecimal.ZERO;
        // Set the number of bags to 4 bags;
        int numBags = 4;

        HashMap<Product, Number[]> order = checkoutController.getOrder();

        // Purchase 2 bags
        checkoutController.purchaseBags(scannerController, newBag, validUnit.getWeight(), 2);
        expectedPrice = expectedPrice.add(newBag.getPrice().multiply(BigDecimal.valueOf(2)));

        // Unblocks the station and lets a new bag be scanned
        checkoutController.baggedItemsValid(scaleController);

        // Purchase 1 bag
        checkoutController.purchaseBags(scannerController, newBag, validUnit.getWeight(), 1);
        expectedPrice = expectedPrice.add(newBag.getPrice().multiply(BigDecimal.valueOf(1)));

        // Unblocks the station and lets a new bag be scanned
        checkoutController.baggedItemsValid(scaleController);

        // Purchase 1 bag
        checkoutController.purchaseBags(scannerController, newBag, validUnit.getWeight(), 1);
        expectedPrice = expectedPrice.add(newBag.getPrice().multiply(BigDecimal.valueOf(1)));

        // Unblocks the station and lets a new bag be scanned
        checkoutController.baggedItemsValid(scaleController);

        // Checking that the bags were added to the order with correct bag numbers and
        // cost
        assertEquals(numBags, order.get(newBag)[0]);
        assertEquals(expectedPrice, checkoutController.getCost());
    }

    /**
     * A method to test that bag is not added when baggingItemLock or systemProtectionLock are true
     */
    @Test
    public void testDisabledLocks() {
        // Set the number of bags
        int numBags = 2;

        // Enables baggingItemLock
        checkoutController.baggingItemLock = true;

        // Adds Bag
        checkoutController.purchaseBags(scannerController, newBag, validUnit.getWeight(), numBags);

        // Bag should not be added, order size should be 0
        assertEquals(0, checkoutController.getOrder().size());

        // Bag should not be added, and the cost should be 0
        assertEquals(BigDecimal.ZERO, checkoutController.getCost());

        // Disables baggingItemLock
        checkoutController.baggingItemLock = false;

        // Enables systemProtectionLock
        checkoutController.systemProtectionLock = true;

        // Adds the bag
        checkoutController.purchaseBags(scannerController, newBag, validUnit.getWeight(), numBags);

        // Bag should not be added, order size should be 0
        assertEquals(0, checkoutController.getOrder().size());

        // Bag should not be added, and the cost should be 0
        assertEquals(BigDecimal.ZERO, checkoutController.getCost());
    }

    /**
     * A method to test that bag is not added when the ItemAdderController is null
     */
    @Test
    public void testPurchaseBags_invalidItemControllerAdder() {
        // Set the number of bags
        int numBags = 2;

        // purchaseBags is called with an invalid ItemControllerAdder
        checkoutController.purchaseBags(null, newBag, validUnit.getWeight(), numBags);

        // Bag should not be added, order size should be 0
        assertEquals(0, checkoutController.getOrder().size());

        // Bag should not be added, and the cost should be 0
        assertEquals(BigDecimal.ZERO, checkoutController.getCost());

    }

    /**
     * Tests purchaseBags with an bag that has an invalid weight, and bag that is null
     */
    @Test
    public void testPurchaseBags_invalidParameters() {
        // set the number of bags
        int numBags = 2;

        // Scan bag with negative weight
        checkoutController.purchaseBags(scannerController, newBag, -1, numBags);

        // Bag should not be added, and order size should be 0
        assertEquals(0, checkoutController.getOrder().size());

        // Bag should not be added, and the cost should be 0
        assertEquals(BigDecimal.ZERO, checkoutController.getCost());

        // Scan null bag
        checkoutController.purchaseBags(scannerController, null, validUnit.getWeight(), numBags);

        // Bag should not be added, and order size should be 0
        assertEquals(0, checkoutController.getOrder().size());

        // Bag should not be added, and the cost should be 0
        assertEquals(BigDecimal.ZERO, checkoutController.getCost());
    }

    /**
     * Tests purchaseBags with an invalid ItemAdderController, and bag that is null
     */
    @Test
    public void testPurchaseBags_nullAdder_nullBag() {
        // Set the number of bags
        int numBags = 2;

        // purchaseBags is called with an invalid ItemControllerAdder and null bag
        checkoutController.purchaseBags(null, null, validUnit.getWeight(), numBags);

        // Bag should not be added, order size should be 0
        assertEquals(0, checkoutController.getOrder().size());

        // Bag should not be added, and the cost should be 0
        assertEquals(BigDecimal.ZERO, checkoutController.getCost());

    }

}
