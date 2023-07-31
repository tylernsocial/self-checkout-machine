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
import com.autovend.devices.DisabledException;
import com.autovend.devices.ElectronicScale;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.BarcodeScannerController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.DeviceController;
import com.autovend.software.controllers.ElectronicScaleController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Set;

import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
/**
 * Test class for the add item, do not add item to bagging area and add item after partial payment use case
 */
public class AddItemTest {

    BarcodeScanner stubScanner;
    ElectronicScale stubScale;
    private CheckoutController checkoutController;
    private BarcodeScannerController scannerController;
    private ElectronicScaleController scaleController;
    private BarcodedProduct databaseItem1;
    private BarcodedProduct databaseItem2;
    private BarcodedUnit validUnit1;
    private BarcodedUnit validUnit2;

    /**
     * Setup for testing
     */
    @Before
    public void setup() {
        checkoutController = new CheckoutController();
        scannerController = new BarcodeScannerController(new BarcodeScanner());
        scaleController = new ElectronicScaleController(new ElectronicScale(1000, 1));

        // First item to be scanned
        databaseItem1 = new BarcodedProduct(new Barcode(Numeral.three, Numeral.three), "test item 1",
                BigDecimal.valueOf(83.29), 359.0);

        // Second item to be scanned
        databaseItem2 = new BarcodedProduct(new Barcode(Numeral.four, Numeral.five), "test item 2",
                BigDecimal.valueOf(42), 60.0);

        validUnit1 = new BarcodedUnit(new Barcode(Numeral.three, Numeral.three), 359.0);
        validUnit2 = new BarcodedUnit(new Barcode(Numeral.four, Numeral.five), 60.0);

        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(databaseItem1.getBarcode(), databaseItem1);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(databaseItem2.getBarcode(), databaseItem2);

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

//	Testing BarcodeScannerController methods

    /**
     * Tests that the BarcodeScannerController reacts correctly to the scan of an
     * item in the database
     */
    @Test
    public void testValidScan() {
        while (!stubScanner.scan(validUnit1)) {
        } // loop until successful scan
        Set<Product> orderSet = scannerController.getMainController().getOrder().keySet();
        Product[] orderArr = orderSet.toArray(new Product[orderSet.size()]);
        assertSame("Scanned product should be in the order list", orderArr[0].getPrice(), databaseItem1.getPrice());
    }

    /**
     * Tests that the BarcodeScannerController reacts correctly to the scan of an
     * item not in the database
     */
    @Test
    public void testNotFoundScan() {
        BarcodedUnit notUnit = new BarcodedUnit(new Barcode(Numeral.three, Numeral.four), 359.0);
        while (!stubScanner.scan(notUnit)) {
        } // loop until successful scan
        assertTrue("Scanned product is not in database so should not be in order list",
                scannerController.getMainController().getOrder().isEmpty());
    }

//	Testing ItemAdderController methods

    /**
     * Tests that the setMainController method of ItemAdderController correctly
     * replaces the controller's main controller and deregisters the controller from
     * the old CheckoutController
     */
    @Test
    public void testNewMainController() {
        CheckoutController newMainController = new CheckoutController();
        scannerController.setMainController(newMainController);

        assertNotSame("New checkout controller should be set in BarcodeScannerController field", checkoutController,
                scannerController.getMainController());
        assertTrue("BarcodeScannerController should be in the new checkout controller's item adder list",
                newMainController.getAllItemAdders().contains(scannerController));
        assertTrue("BarcodeScannerController should not be in the old checkout controller's item adder list",
                checkoutController.getAllItemAdders().isEmpty());
    }

//	Testing DeviceController methods

    /**
     * Tests that the disableDevice method of DeviceController causes a
     * DisabledException to be thrown when a scan is attempted
     */
    @Test(expected = DisabledException.class)
    public void testDisabledScanController() {
        scannerController.disableDevice();
        stubScanner.scan(validUnit1);
    }

    /**
     * Tests that the enableDevice method of DeviceController works correctly,
     * allowing scans to take place again
     */
    @Test
    public void testReenabledScanController() {
        scannerController.disableDevice();
        scannerController.enableDevice();
        while (!stubScanner.scan(validUnit1)) {
        } // loop until successful scan
        Set<Product> orderSet = scannerController.getMainController().getOrder().keySet();
        Product[] orderArr = orderSet.toArray(new Product[orderSet.size()]);
        assertSame("Scanned product should be in the order list", orderArr[0].getPrice(), databaseItem1.getPrice());
    }

    /**
     * Tests that the setDevice method of DeviceController correctly replaces the
     * old BarcodeScanner with the new one
     */
    @Test
    public void testNewScanner() {
        BarcodeScanner newScanner = new BarcodeScanner();
        scannerController.setDevice(newScanner);
        assertNotSame("New barcode scanner should be ..", stubScanner, scannerController.getDevice());
    }

// Testing ElectronicScaleController

    /**
     * Tests that the ElectronicScaleController reacts correctly to adding items to
     * order.
     */
    @Test
    public void testScaleScanLock() {
        while (!stubScanner.scan(validUnit1)) {
        } // loop until successful scan
        HashMap<Product, Number[]> order = scannerController.getMainController().getOrder();
        // getting amount of first item in order
        int count = order.get(databaseItem1)[0].intValue();
        assertEquals("Only 1 copy of the item should be added to the order", 1, count);

        // scan an item again and verify that the order wasn't updated since it hasn't
        // been added to
        // the bagging area.
        while (!stubScanner.scan(validUnit1)) {
        }
        count = order.get(databaseItem1)[0].intValue();
        assertEquals("Item wasn't added to scale yet so should be still 1", 1, count);
        stubScale.add(validUnit1);
        while (!stubScanner.scan(validUnit1)) {
        }
        count = order.get(databaseItem1)[0].intValue();
        assertEquals("Since item was put on scale, it should count 2 copies of product", 2, count);
    }

    @Test
    public void testScaleIncorrectWeightScanLock() {
        BarcodedUnit validUnit2 = new BarcodedUnit(new Barcode(Numeral.three, Numeral.three), 500.0);

        while (!stubScanner.scan(validUnit2)) {
        } // loop until successful scan
        HashMap<Product, Number[]> order = scannerController.getMainController().getOrder();
        // getting amount of first item in order
        int count = order.get(databaseItem1)[0].intValue();
        assertEquals("Only 1 copy of the item should be added to the order", 1, count);
        // add item to bagging area then verify that since the weight is off by so much,
        // it shouldn't add another
        // to the count.
        stubScale.add(validUnit2);
        while (!stubScanner.scan(validUnit2)) {
        }
        count = order.get(databaseItem1)[0].intValue();
        assertEquals("Since item was put on scale, it should count 2 copies of product", 1, count);
        validUnit2 = null;
    }

    @Test
    public void testDiscrepancyResolved() {
        scaleController.resetOrder();
        scaleController.attendantInput(true);
        scaleController.reactToWeightChangedEvent(stubScale, 10.0);
        assertFalse(scaleController.getMainController().baggingItemLock);
    }

    @Test
    public void testDiscrepancUnesolved() {
        scaleController.resetOrder();
        scaleController.attendantInput(false);
        scaleController.reactToWeightChangedEvent(stubScale, 10.0);
        assertTrue(scaleController.getMainController().baggingItemLock);
    }

    @Test
    public void testScaleErrorLock() {
        BarcodedUnit validUnit2 = new BarcodedUnit(new Barcode(Numeral.three, Numeral.three), 100000.0);

        while (!stubScanner.scan(validUnit2)) {
        } // loop until successful scan
        // add item to bagging area then verify that the error lock to avoid damage to
        // the scale
        // is true, and that taking it off would end that
        stubScale.add(validUnit2);
        assertTrue(checkoutController.systemProtectionLock);
        stubScale.remove(validUnit2);
        assertFalse(checkoutController.systemProtectionLock);
        validUnit2 = null;
    }

//	Testing ElectronicScaleController methods

    /**
     * Tests that the setMainController method of ElectronicScaleController
     * correctly replaces the controller's main controller and deregisters the
     * controller from the old CheckoutController
     */
    @Test
    public void testNewMainControllerScale() {
        CheckoutController newMainController = new CheckoutController();
        scaleController.setMainController(newMainController);

        assertNotSame("New checkout controller should be set in ElectronicScaleController field", checkoutController,
                scaleController.getMainController());
        assertTrue("ElectronicScaleController should be in the new checkout controller's bagging controller list",
                newMainController.getAllBaggingControllers().contains(scaleController));
        assertTrue("ElectronicScaleController should not be in the old checkout controller's bagging controller list",
                checkoutController.getAllBaggingControllers().isEmpty());
    }

    /**
     * Testing that the checkout only has the scale and scanner controllers as
     * peripherals
     */

    @Test
    public void testCorrectRegistrationControllers() {
        Set<DeviceController> controllers = checkoutController.getAllDeviceControllers();
        assertTrue("Only controllers should be scale and scanner controller", controllers.contains(scaleController));
        assertTrue("Only controllers should be scale and scanner controller", controllers.contains(scannerController));
        assertEquals("Only controllers should be scale and scanner controller", controllers.size(), 2);

    }

    /**
     * Tests addItem by adding two items
     */
    @Test
    public void testAddItem() {

        // Adds item
        checkoutController.addItem(scannerController, databaseItem1, validUnit1.getWeight());

        // Adds the cost of the first item to the total
        BigDecimal total = databaseItem1.getPrice();

        // Checks that the item was added and the order was updated to 1
        assertEquals(1, checkoutController.getOrder().size());

        // Checks that the total cost was updated
        assertEquals(total, checkoutController.getCost());

        // Unblocks the station and lets a new item be scanned
        checkoutController.baggedItemsValid(scaleController);

        // Adds a second item
        checkoutController.addItem(scannerController, databaseItem2, validUnit2.getWeight());

        // Adds the cost of the second item to the total
        total = total.add(databaseItem2.getPrice());

        // Rounds the value to 2 decimal places
        total = total.setScale(2, RoundingMode.HALF_UP);

        // Checks that the item was added and the order was updated to 2
        assertEquals(2, checkoutController.getOrder().size());

        // Checks that the total cost was updated
        assertEquals(total, checkoutController.getCost());
    }

    /**
     * Tests addItem with an item that has an invalid weight, and an item that is
     * null
     */
    @Test
    public void testAddItemWithInvalidParameters() {
        // Scan item with negative weight
        checkoutController.addItem(scannerController, databaseItem1, -1);

        // Item should not be added, and order size should be 0
        assertEquals(0, checkoutController.getOrder().size());

        // Item should not be added, and the cost should be 0
        assertEquals(BigDecimal.ZERO, checkoutController.getCost());

        // Scan null item
        checkoutController.addItem(scannerController, null, validUnit1.getWeight());

        // Item should not be added, and order size should be 0
        assertEquals(0, checkoutController.getOrder().size());

        // Item should not be added, and the cost should be 0
        assertEquals(BigDecimal.ZERO, checkoutController.getCost());
    }

    /**
     * Test the remaining amount after two partial payments
     */
    @Test
    public void testGetRemainingAmount() {

        // First Item is scanned
        checkoutController.addItem(scannerController, databaseItem1, validUnit1.getWeight());

        // Adds the cost of the first item to the total
        BigDecimal total = databaseItem1.getPrice();

        // Simulates the item being put on the bagging area and lets us scan another
        // item.
        checkoutController.baggedItemsValid(scaleController);

        // First item is added
        checkoutController.addItem(scannerController, databaseItem2, validUnit2.getWeight());

        // Adds the cost of the second item to the total
        total = total.add(databaseItem2.getPrice());

        // Rounds the value to 2 decimal places
        total = total.setScale(2, RoundingMode.HALF_UP);

        // Amount paid is updated
        checkoutController.addToAmountPaid(BigDecimal.valueOf(50));

        // Subtracts the amount paid from the total
        total = total.subtract(BigDecimal.valueOf(50));

        // Rounds the value to 2 decimal places
        total = total.setScale(2, RoundingMode.HALF_UP);

        // Checks that amount to be paid is the total unpaid amount
        assertEquals(total, checkoutController.getRemainingAmount());

        // Amount paid is updated
        checkoutController.addToAmountPaid(BigDecimal.valueOf(75.29));

        // Subtracts the amount paid from the total
        total = total.subtract(BigDecimal.valueOf(75.29));

        // Rounds the value to 2 decimal places
        total = total.setScale(2, RoundingMode.HALF_UP);

        // Checks that amount to be paid is the total unpaid amount
        assertEquals(total, checkoutController.getRemainingAmount());
    }

    /**
     * A method to test if getRemaining amount is zero without any items
     */
    @Test
    public void testGetRemainingAmountWithNoItems() {
        assertEquals(BigDecimal.ZERO, checkoutController.getRemainingAmount());
    }

    /**
     * A method to test that item is not added when baggingItemLock or
     * systemProtectionLock are true
     */
    @Test
    public void testDisabledLocks() {

        // Enables baggingItemLock
        checkoutController.baggingItemLock = true;

        // Adds item
        checkoutController.addItem(scannerController, databaseItem1, validUnit1.getWeight());

        // Item should not be added, order size should be 0
        assertEquals(0, checkoutController.getOrder().size());

        // Item should not be added, and the cost should be 0
        assertEquals(BigDecimal.ZERO, checkoutController.getCost());

        // Disables baggingItemLock
        checkoutController.baggingItemLock = false;

        // Enables systemProtectionLock
        checkoutController.systemProtectionLock = true;

        // Adds item
        checkoutController.addItem(scannerController, databaseItem1, validUnit1.getWeight());

        // Item should not be added, order size should be 0
        assertEquals(0, checkoutController.getOrder().size());

        // Item should not be added, and the cost should be 0
        assertEquals(BigDecimal.ZERO, checkoutController.getCost());
    }

    /**
     * A method to test that item is not added when the ItemAdderController is not
     * valid
     */

    @Test
    public void testInvalidItemControllerAdder() {

        // addItem is called with an invalid ItemControllerAdder
        checkoutController.addItem(null, databaseItem1, validUnit1.getWeight());

        // Item should not be added, order size should be 0
        assertEquals(0, checkoutController.getOrder().size());

        // Item should not be added, and the cost should be 0
        assertEquals(BigDecimal.ZERO, checkoutController.getCost());

    }

    /**
     * A method to test that more than one of the same item is added correctly
     */
    @Test
    public void testAddingDuplicateItems() {

        // Stores the item information
        HashMap<Product, Number[]> order = checkoutController.getOrder();

        // Add the same bag to the order
        checkoutController.addItem(scannerController, databaseItem1, validUnit1.getWeight());

        // Adds the cost of the first item to the total
        BigDecimal total = databaseItem1.getPrice();

        // Check that the item number and cost in the order were updated correctly
        assertEquals(1, order.get(databaseItem1)[0]);
        assertEquals(total, checkoutController.getCost());

        // Unblocks the station and lets a new item be scanned
        checkoutController.baggedItemsValid(scaleController);

        // Add another of the same item to the order
        checkoutController.addItem(scannerController, databaseItem1, validUnit1.getWeight());

        // Adds the cost of the second item to the total
        total = total.add(databaseItem1.getPrice());

        // Rounds the value to 2 decimal places
        total = total.setScale(2, RoundingMode.HALF_UP);

        // Check that the item number and cost in the order were updated correctly
        assertEquals(2, order.get(databaseItem1)[0]);
        assertEquals(total, checkoutController.getCost());

    }


    /**
     * Test that you can scan after you don't add to the bagging area
     */
    @Test
    public void dontAddItemToBaggingArea() {
        double unitWeight = 3.0;

        BarcodedUnit validUnit2 = new BarcodedUnit(new Barcode(Numeral.three, Numeral.three), unitWeight);

        while (!stubScanner.scan(validUnit2)) {
        }
        scaleController.doNotAddItemToBaggingArea(stubScale, unitWeight);
        scaleController.attendantInput(true);
        //while (!stubScanner.scan(validUnit1)) {
        //}
        //	stubScale.add(validUnit1);
        assertEquals(scaleController.getExpectedWeight(), scaleController.getCurrentWeight(), 0);
    }

}