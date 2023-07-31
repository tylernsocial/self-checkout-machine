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
import com.autovend.devices.CardReader;
import com.autovend.devices.ElectronicScale;
import com.autovend.devices.SimulationException;
import com.autovend.external.CardIssuer;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.software.controllers.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Scanner;

import static org.junit.Assert.*;


@SuppressWarnings("unused")

public class TestMembershipCardController {
    TestBank bankStub;
    CheckoutController controllerStub;
    CreditCard cardStub;
    CardReader cardReaderStub;
    CardReaderController readerControllerStub;
    BarcodeScanner stubScanner;
    ElectronicScale stubScale;
    MembershipCard mCardStub;
    MembershipCardController mcc = new MembershipCardController();
    Scanner scanner = new Scanner(System.in);
    InputStreamReader inputReader;
    CardReader cr = new CardReader();
    CardReaderController crc = new CardReaderController(cr);
    Card membershipCard;
    private CheckoutController checkoutController;
    private BarcodeScannerController scannerController;
    private ElectronicScaleController scaleController;
    private BarcodedProduct databaseItem1;
    private BarcodedProduct databaseItem2;
    private BarcodedUnit validUnit1;
    private BarcodedUnit validUnit2;

    @Before
    public void setup() {
        membershipCard = new MembershipCard("Membership", "123123123123", "XZ", true);
        bankStub = new TestBank("TestBank");
        cardStub = new CreditCard(
                "Credit Card", "12345", "Steve", "987", "1337", true, true
        );
        mCardStub = new MembershipCard("Membership", "123123123123", "XZ", true);
        controllerStub = new CheckoutController();
        cardReaderStub = new CardReader();
        readerControllerStub = new CardReaderController(cardReaderStub);
        controllerStub.registerPaymentController(readerControllerStub);
        readerControllerStub.disableDevice();
        readerControllerStub.setMainController(controllerStub);


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

    @After
    public void teardown() {
        membershipCard = null;
        bankStub = null;
        cardStub = null;
        mCardStub = null;
        controllerStub = null;
        cardReaderStub = null;
        checkoutController = null;
        scannerController = null;
        scaleController = null;
        stubScanner = null;
        validUnit1 = null;
        validUnit2 = null;
        databaseItem1 = null;
        databaseItem2 = null;
        mcc = null;
        scanner = null;
        inputReader = null;
        cr = null;
        crc = null;

    }

    @Test
    public void testIsValidNullValue() throws IllegalDigitException {
        String expectedMessage = "The Membership number should be exactly 12 digits long.";
        Exception exception = assertThrows(IllegalDigitException.class, () -> mcc.isValid(null));
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    public void testIsValidLessDigits() throws IllegalDigitException {
        String expectedMessage = "The Membership number should be exactly 12 digits long.";
        Exception exception = assertThrows(IllegalDigitException.class, () -> mcc.isValid("123456"));
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testIsValidNan() throws IllegalDigitException {
        String expectedMessage = "The Membership number should only contain digits between 0-9.";
        Exception exception = assertThrows(IllegalDigitException.class,
                () -> mcc.isValid("abc234567890"));
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testIsValidReturnsTrue() {
        boolean expected = true;
        boolean actual = mcc.isValid("564823890124");
        assertEquals(expected, actual);
    }

    @Test
    public void testTapMembershipCard() throws IOException {

        controllerStub.inputMembership = true;
        cardReaderStub.tap(mCardStub);
        String mNum = controllerStub.getMembershipNum();

        assertEquals("123123123123", mNum);
        assertFalse(controllerStub.inputMembership);
        assertTrue(controllerStub.existedMembership);
    }

    @Test
    public void TestMembershipCardByScan() {

        checkoutController.inputMembership = true;
        BarcodedUnit notUnit = new BarcodedUnit(new Barcode(Numeral.three, Numeral.four, Numeral.three, Numeral.four, Numeral.three, Numeral.four, Numeral.three, Numeral.four, Numeral.three, Numeral.four, Numeral.three, Numeral.four), 359.0);
        stubScanner.scan(notUnit);
        assertEquals("343434343434", checkoutController.membershipNum);
        assertFalse(checkoutController.inputMembership);
        assertTrue(checkoutController.existedMembership);
    }

    @Test
    public void testSwipeMembershipCard() throws IOException {

        controllerStub.inputMembership = true;
        cardReaderStub.swipe(mCardStub, null);
        String mNum = controllerStub.getMembershipNum();

        assertEquals("123123123123", mNum);
        assertFalse(controllerStub.inputMembership);
        assertTrue(controllerStub.existedMembership);
    }

    private class TestBank extends CardIssuer {
        public boolean held;
        public boolean posted;
        public boolean noPostCall;
        public boolean noHoldCall;
        public boolean holdAuthorized;
        public boolean canPostTransaction;

        /**
         * Create a card provider.
         *
         * @param name The company's name.
         * @throws SimulationException If name is null.
         */
        public TestBank(String name) {
            super(name);
            noHoldCall = true;
            noPostCall = true;
        }

        public int authorizeHold(String cardNumber, BigDecimal amount) {
            if (holdAuthorized) {
                held = true;
                return 1;
            } else {
                held = false;
                return -1;
            }
        }

        public boolean postTransaction(String cardNumber, int holdNumber, BigDecimal actualAmount) {
            noPostCall = false;
            if (holdNumber == 1 && canPostTransaction) {
                this.posted = true;
                return true;
            } else {
                this.posted = false;
                return false;
            }
        }

    }

}
