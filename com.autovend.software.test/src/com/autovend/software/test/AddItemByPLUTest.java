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
/*
package com.autovend.software.test;

import com.autovend.products.PLUCodedProduct;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.ItemAdderController;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class AddItemByPLUTest {

    private CheckoutController checkoutController;
    private Map<String, PLUCodedProduct> pluProductDatabase;
    private StubItemAdderController stubItemAdderController;

    // Stub class for simulating the database
    public class StubDatabase {
        public final Map<String, PLUCodedProduct> PLU_PRODUCT_DATABASE = new HashMap<>();
    }

    // Stub class for ItemAdderController
    public class StubItemAdderController extends ItemAdderController {

        public StubItemAdderController() {
            super(null);
        }
    }

    @Before
    public void setUp() {
        pluProductDatabase = new HashMap<>();
        pluProductDatabase.put("1234", new PLUCodedProduct("1234", "Apple", new BigDecimal("1.99")));
        pluProductDatabase.put("5678", new PLUCodedProduct("5678", "Banana", new BigDecimal("0.99")));
        pluProductDatabase.put("9012", new PLUCodedProduct("9012", "Orange", new BigDecimal("1.49")));

        StubDatabase stubDatabase = new StubDatabase();
        stubDatabase.PLU_PRODUCT_DATABASE.putAll(pluProductDatabase);

        checkoutController = new CheckoutController(stubDatabase);
        stubItemAdderController = new StubItemAdderController();
    }

    @Test
    public void testAddItemByPLU() {
        String pluCode = "1234";
        BigDecimal expectedWeight = new BigDecimal("0.5");
        checkoutController.addItemByPLU(stubItemAdderController, pluCode, expectedWeight.toString());

        // Add assertions to validate the expected behavior
        // For example, check if the item is added to the bill or the expected weight is updated
    }

    @Test(expected = NoSuchElementException.class)
    public void testAddItemByPLUInvalidCode() {
        String invalidPLUCode = "1111";
        BigDecimal expectedWeight = new BigDecimal("0.5");
        checkoutController.addItemByPLU(stubItemAdderController, invalidPLUCode, expectedWeight.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddItemByPLUWithNegativeWeight() {
        String pluCode = "1234";
        BigDecimal negativeWeight = new BigDecimal("-0.5");
        checkoutController.addItemByPLU(stubItemAdderController, pluCode, negativeWeight.toString());
    }
}
*/