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
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.software.controllers.BaggingAreaController;
import com.autovend.software.controllers.BarcodeScannerController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.ElectronicScaleController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("rawtypes")

public class AddOwnBagsTest {
    BarcodeScannerController scannerController;
    BarcodeScanner stubScanner;

    ElectronicScaleController scaleController;
    ElectronicScale stubScale;
    BarcodedProduct databaseItem;

    BarcodedUnit validUnit;
    BarcodedUnit bag;
    CheckoutController checkoutController;
    SelfCheckoutStation stubStation;

    /**
     * Set up of objects, variables etc.. that happens before tests
     */
    @Before
    public void setup() {
        SelfCheckoutStation stubStation = new SelfCheckoutStation(Currency.getInstance("CAD"),
                new int[]{5, 10, 20, 50, 100},
                new BigDecimal[]{new BigDecimal(25), new BigDecimal(100), new BigDecimal(5)}, 1000, 1);
        databaseItem = new BarcodedProduct(new Barcode(Numeral.three, Numeral.three), "test item",
                BigDecimal.valueOf(83.29), 359.0);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(databaseItem.getBarcode(), databaseItem);
        validUnit = new BarcodedUnit(new Barcode(Numeral.three, Numeral.three), 76.0);
        bag = new BarcodedUnit(new Barcode(Numeral.three, Numeral.three), 0.75);

        stubScanner = stubStation.mainScanner;
        stubScale = stubStation.baggingArea;
        checkoutController = new CheckoutController(stubStation);

        scannerController = new BarcodeScannerController(stubScanner);
        scannerController.setMainController(checkoutController);
        scannerController.enableDevice();

        scaleController = new ElectronicScaleController(stubScale);
        scaleController.setMainController(checkoutController);
        scaleController.enableDevice();

        stubScanner.register(scannerController);
        stubScale.register(scaleController);
    }

    /**
     * Tears down objects so they can be initialized again with setup
     */
    @After
    public void teardown() {
        stubScanner = null;
        checkoutController = null;
        scannerController = null;
        scaleController = null;
        stubScale = null;
        stubStation = null;
    }

    /**
     * Tests if the system is locked when the Attendant does not approve due to a
     * weight discrepancy Expected: system should stay locked if the attendant does
     * not approve
     */
    @Test
    public void addOwnBags_attendantNotApproved_lockedStation() {
        stubScanner.scan(validUnit);
        checkoutController.addOwnBags();
        checkoutController.AttendantApproved = false;
        assertTrue(checkoutController.systemProtectionLock);
    }

    /**
     * Checks to see if the system is unlocked when the Attendant does approve due
     * to a lack of a weight discrepancy Expected: system should be unlocked if
     * attendant does approve
     */
    @Test
    public void addOwnBags_attendantApproved_UnlockedStation() {
        stubScanner.scan(validUnit);
        checkoutController.AttendantApproved = true;
        checkoutController.addOwnBags();
        assertFalse(checkoutController.systemProtectionLock);
    }

    /**
     * Checks to see if the bagging area with bags is greater than the bagging area
     * without bags Expected: weight of bagging area with bags should be greater
     * than the weight of the bagging area without bags
     */
    @Test
    public void addOwnBags_WeightWithBagsGreaterWeight() {
        boolean bigger = true;

        checkoutController.addOwnBags();
        Map<BaggingAreaController, Double> weight = checkoutController.getWeight();
        Map<BaggingAreaController, Double> weightWithBags = checkoutController.getWeightWithBags();
        for (BaggingAreaController controller : weightWithBags.keySet()) {
            weightWithBags.put(controller, 0.1);

            double weight1 = weight.get(controller);
            double weight2 = weightWithBags.get(controller);

            if (weight2 > weight1) {
                bigger = true;
            }
        }

        assertTrue(bigger);
    }

    /**
     * Checks to see if the hashMap that contains the weight without bags is ready
     * to add bags, while the bagging area with no bags is not Expected: as above
     */
    @Test
    public void addOwnBags_ChecksSetAddingBags() {
        stubScanner.scan(validUnit);
        checkoutController.addOwnBags();

        boolean value = false;

        checkoutController.addOwnBags();
        Map<BaggingAreaController, Double> weight = checkoutController.getWeight();
        Map<BaggingAreaController, Double> weightWithBags = checkoutController.getWeightWithBags();

        for (BaggingAreaController controller : weightWithBags.keySet()) {
            ElectronicScaleController scaleController1 = (ElectronicScaleController) controller;
            scaleController1.setAddingBags(true);
            boolean value1 = scaleController1.getAddingBags();
            ElectronicScaleController scaleController2 = (ElectronicScaleController) controller;
            scaleController1.setAddingBags(false);
            boolean value2 = scaleController2.getAddingBags();

            if (value1 != value2) {
                value = true;
            }
        }

        assertTrue(value);
    }

    /**
     * Checks to see if the bagging area with weight without bags is set to add bags
     * Expected: should be true to be set
     */
    @Test
    public void addOwnBags_settingBags1() {
        checkoutController.addOwnBags();

        boolean value = false;

        Map<BaggingAreaController, Double> weight = checkoutController.getWeight();
        for (BaggingAreaController controller : weight.keySet()) {
            ElectronicScaleController scaleController = (ElectronicScaleController) controller;
            scaleController.setAddingBags(true);
            value = scaleController.getAddingBags();
        }

        assertTrue(value);
    }

    /**
     * Checks to see if the bagging area with weight with bags is not set to add
     * bags Expected: should be false to be set
     */
    @Test
    public void addOwnBags_settingBags2() {
        stubScanner.scan(validUnit);
        checkoutController.addOwnBags();

        boolean value = false;

        Map<BaggingAreaController, Double> weightWithBags = checkoutController.getWeightWithBags();
        for (BaggingAreaController controller : weightWithBags.keySet()) {
            ElectronicScaleController scaleController = (ElectronicScaleController) controller;
            value = scaleController.getAddingBags();
        }

        assertFalse(value);
    }

    /**
     * Checks to see if the updated expected weight of the bagging area without bags
     * is less than the updated expected weight with bags
     */
    @Test
    public void addOwnBags_updateWeightCheck() {
        stubScanner.scan(validUnit);
        boolean value = false;
        HashSet<BaggingAreaController> validBaggingControllersBefore = checkoutController.getValidBaggingControllers();
        double expectedWeightBeforeBags = 0.0;

        for (BaggingAreaController baggingController : validBaggingControllersBefore) {
            ElectronicScaleController scale = (ElectronicScaleController) baggingController;
            expectedWeightBeforeBags += scale.getExpectedWeight();
        }

        double expectedWeightAfterBags = 0.0;

        checkoutController.addOwnBags();
        HashSet<BaggingAreaController> validBaggingControllersAfter = checkoutController.getValidBaggingControllers();

        for (BaggingAreaController baggingController : validBaggingControllersBefore) {
            ElectronicScaleController scale = (ElectronicScaleController) baggingController;
            scale.updateWithBagWeight(0.1);
            expectedWeightAfterBags += scale.getExpectedWeight();
        }

        if (expectedWeightAfterBags > expectedWeightBeforeBags) {
            value = true;
        }

        assertTrue(value);

    }

}