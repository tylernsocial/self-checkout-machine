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
/**
 *
 */
package com.autovend.software.test;

import com.autovend.Barcode;
import com.autovend.BarcodedUnit;
import com.autovend.Numeral;
import com.autovend.SellableUnit;
import com.autovend.devices.ElectronicScale;
import com.autovend.devices.SimulationException;
import com.autovend.devices.TouchScreen;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.software.controllers.AddByBrowseItemController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.ElectronicScaleController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * @author hossa
 */
public class AddByBrowseItemControllerTest {
    TouchScreen stubSreen;
    ElectronicScale stubScale;
    BarcodedProduct testProduct;
    BigDecimal testprice = new BigDecimal("55.0");
    Barcode testBarcode;
    BarcodedUnit testProductUnit;
    SellableUnit testSellableProduct;
    int inventory = 5;
    private AddByBrowseItemController browseItemController;
    private CheckoutController checkoutController;
    private ElectronicScaleController scaleController;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        //eScale = new ElectronicScale(1000, 1);
        checkoutController = new CheckoutController();
        browseItemController = new AddByBrowseItemController(new TouchScreen());
        scaleController = new ElectronicScaleController(new ElectronicScale(1000, 1));

        // add barcoded product
        testBarcode = new Barcode(Numeral.zero, Numeral.one, Numeral.two);

        testProduct = new BarcodedProduct(testBarcode, "Test Product", testprice, 10.0);

        testSellableProduct = new BarcodedUnit(testBarcode, 10.0);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(testBarcode, testProduct);

        stubSreen = new TouchScreen();
        stubScale = new ElectronicScale(1000, 1);

        browseItemController.setMainController(checkoutController);
        scaleController = new ElectronicScaleController(stubScale);
        scaleController.setMainController(checkoutController);

        stubSreen.register(browseItemController);
        stubScale.register(scaleController);

    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {


        testBarcode = null;
        testProduct = null;
        //testProduct2 = null;
        testSellableProduct = null;
        ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();

    }

    @Test
    public void AddByBrowsingEventTest() {
        System.out.println("test");
        String Item = "Test Product";
        BigDecimal total = testProduct.getPrice();

        //add item by browse
        browseItemController.AddByBrowsingEvent(Item);

        //test if item has been added to order
        assertEquals(1, checkoutController.getOrder().size());
        //test if the cost order has updated
        assertEquals(total, checkoutController.getCost());


    }

    @Test(expected = SimulationException.class)
    public void AddByBrowsingEventTestNullProduct() {
        System.out.println("test2");
        String Item = "Test Product2";
        BigDecimal total = testProduct.getPrice();

        //add item by browse
        browseItemController.AddByBrowsingEvent(Item);


    }
}
