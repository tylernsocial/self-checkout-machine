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

import com.autovend.CreditCard;
import com.autovend.DebitCard;
import com.autovend.GiftCard;
import com.autovend.devices.CardReader;
import com.autovend.devices.SimulationException;
import com.autovend.external.CardIssuer;
import com.autovend.software.controllers.CardReaderController;
import com.autovend.software.controllers.CheckoutController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.*;

public class CardPaymentTest {
    TestBank bankStub;
    CheckoutController controllerStub;
    CreditCard cardStub;
    DebitCard debitStub;
    GiftCard giftStub;
    Currency localC;
    CardReader cardReaderStub;
    CardReaderController readerControllerStub;

    @Before
    public void setup() {
        bankStub = new TestBank("TestBank");
        cardStub = new CreditCard(
                "Credit Card", "12345", "Steve", "987", "1337", true, true
        );
        debitStub = new DebitCard(
                "Debit Card", "12345", "Steve", "987", "1337", true, true
        );
        Currency localC = Currency.getInstance("CAD");
        giftStub = new GiftCard("Gift Card", "12345", "1313", localC, BigDecimal.valueOf(10));
        controllerStub = new CheckoutController();
        cardReaderStub = new CardReader();
        readerControllerStub = new CardReaderController(cardReaderStub);
        controllerStub.registerPaymentController(readerControllerStub);
        readerControllerStub.disableDevice();
        readerControllerStub.setMainController(controllerStub);
    }

    @Test
    public void testSuccessfulTransaction() {
        assertTrue(cardReaderStub.isDisabled());
        readerControllerStub.enablePayment(bankStub, cardStub, BigDecimal.ONE);
        assertFalse(cardReaderStub.isDisabled());
        readerControllerStub.card = cardStub;
        bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        try {
            cardReaderStub.insert(cardStub, "1337");
        } catch (Exception ex) {
            fail("Exception incorrectly thrown");
        }
        assertTrue(bankStub.held);
        assertTrue(bankStub.posted);
        cardReaderStub.remove();
        assertFalse(readerControllerStub.isPaying);
        assertEquals(controllerStub.getRemainingAmount(), BigDecimal.valueOf(-1));
        assertTrue(cardReaderStub.isDisabled());
        readerControllerStub.card = null;
    }

    @Test
    public void testPostFailed() {
        assertTrue(cardReaderStub.isDisabled());
        readerControllerStub.enablePayment(bankStub, cardStub, BigDecimal.ONE);
        assertFalse(cardReaderStub.isDisabled());
        bankStub.canPostTransaction = false;
        readerControllerStub.card = cardStub;
        bankStub.holdAuthorized = true;
        try {
            cardReaderStub.insert(cardStub, "1337");
        } catch (Exception ex) {
            fail("Exception incorrectly thrown");
        }
        assertTrue(readerControllerStub.isPaying);
        assertTrue(bankStub.held);
        assertFalse(bankStub.posted);
        cardReaderStub.remove();
        assertFalse(readerControllerStub.isPaying);
        assertEquals(controllerStub.getRemainingAmount(), BigDecimal.valueOf(0));
        readerControllerStub.card = null;
    }

    @Test
    public void testHoldFailed() {
        assertTrue(cardReaderStub.isDisabled());
        readerControllerStub.enablePayment(bankStub, cardStub, BigDecimal.ONE);
        assertFalse(cardReaderStub.isDisabled());
        readerControllerStub.card = cardStub;
        bankStub.canPostTransaction = false;
        bankStub.holdAuthorized = false;
        try {
            cardReaderStub.insert(cardStub, "1337");
        } catch (Exception ex) {
            fail("Exception incorrectly thrown");
        }
        assertFalse(bankStub.held);
        assertTrue(bankStub.noPostCall);
        assertEquals(controllerStub.getRemainingAmount(), BigDecimal.valueOf(0));
        readerControllerStub.card = null;
    }

    @Test
    public void testNotEnabled() {
        assertTrue(cardReaderStub.isDisabled());
        bankStub.canPostTransaction = false;
        bankStub.holdAuthorized = false;
        try {
            readerControllerStub.data = cardReaderStub.insert(cardStub, "1337");
        } catch (Exception ex) {
            fail("Exception incorrectly thrown");
        }
        assertTrue(bankStub.noHoldCall);
        assertTrue(bankStub.noPostCall);
        assertEquals(controllerStub.getRemainingAmount(), BigDecimal.valueOf(0));
        assertTrue(cardReaderStub.isDisabled());
    }

    @Test
    public void testTapFail() {
        assertTrue(cardReaderStub.isDisabled());
        bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        try {
            cardReaderStub.tap(cardStub);
        } catch (Exception ex) {
            fail("Exception incorrectly thrown");
        }
        assertTrue(bankStub.noHoldCall);
        assertTrue(bankStub.noPostCall);
        assertEquals(controllerStub.getRemainingAmount(), BigDecimal.valueOf(0));
        assertTrue(cardReaderStub.isDisabled());
    }

    /**
     * Since we also need to test whether PayByCard works in checkout controller, it
     * makes sense to do so here
     */

    @Test
    public void payByCardTestSuccess() {
        assertFalse(readerControllerStub.isPaying);
        controllerStub.cost = BigDecimal.ONE;
        controllerStub.payByCard(bankStub, BigDecimal.ONE, cardStub);
        assertFalse(cardReaderStub.isDisabled());
        assertEquals(readerControllerStub.bank, bankStub);
    }

    @Test
    public void payByCardTestSystemProtectionLock() {
        assertTrue(cardReaderStub.isDisabled());
        controllerStub.cost = BigDecimal.ONE;
        controllerStub.systemProtectionLock = true;
        controllerStub.payByCard(bankStub, BigDecimal.ONE, cardStub);
        assertTrue(cardReaderStub.isDisabled());
    }

    @Test
    public void payByCardTestBaggingLock() {
        assertTrue(cardReaderStub.isDisabled());
        controllerStub.cost = BigDecimal.ONE;
        controllerStub.baggingItemLock = true;
        controllerStub.payByCard(bankStub, BigDecimal.ONE, cardStub);
        assertTrue(cardReaderStub.isDisabled());
    }

    @Test
    public void payByCardTestNullBank() {
        assertTrue(cardReaderStub.isDisabled());
        controllerStub.cost = BigDecimal.ONE;
        controllerStub.payByCard(null, BigDecimal.ONE, cardStub);
        assertTrue(cardReaderStub.isDisabled());
    }

    @Test
    public void payByCardTestPayMoreThanOrderCost() {
        assertTrue(cardReaderStub.isDisabled());
        controllerStub.cost = BigDecimal.ZERO;
        controllerStub.payByCard(null, BigDecimal.ONE, cardStub);
        assertTrue(cardReaderStub.isDisabled());
    }

    @Test
    public void payWithTap() {
        assertTrue(cardReaderStub.isDisabled());
        readerControllerStub.enablePayment(bankStub, cardStub, BigDecimal.ONE);
        assertFalse(cardReaderStub.isDisabled());
        readerControllerStub.card = cardStub;
        bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        try {
            cardReaderStub.tap(cardStub);
        } catch (Exception ex) {
            fail("Exception incorrectly thrown");
        }
        assertTrue(bankStub.held);
        assertTrue(bankStub.posted);
        assertFalse(readerControllerStub.isPaying);
        assertEquals(controllerStub.getRemainingAmount(), BigDecimal.valueOf(-1));
        assertTrue(cardReaderStub.isDisabled());
        readerControllerStub.card = null;
    }

    @Test
    public void payWithTapDebit() {
        assertTrue(cardReaderStub.isDisabled());
        readerControllerStub.enablePayment(bankStub, debitStub, BigDecimal.ONE);
        assertFalse(cardReaderStub.isDisabled());
        readerControllerStub.card = debitStub;
        bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        try {
            cardReaderStub.tap(debitStub);
        } catch (Exception ex) {
            fail("Exception incorrectly thrown");
        }
        assertTrue(bankStub.held);
        assertTrue(bankStub.posted);
        assertFalse(readerControllerStub.isPaying);
        assertEquals(controllerStub.getRemainingAmount(), BigDecimal.valueOf(-1));
        assertTrue(cardReaderStub.isDisabled());
        readerControllerStub.card = null;
    }

    @Test
    public void payWithInsertDebit() {
        assertTrue(cardReaderStub.isDisabled());
        readerControllerStub.enablePayment(bankStub, debitStub, BigDecimal.ONE);
        assertFalse(cardReaderStub.isDisabled());
        readerControllerStub.card = debitStub;
        bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        try {
            cardReaderStub.insert(debitStub, "1337");
        } catch (Exception ex) {
            fail("Exception incorrectly thrown");
        }
        assertTrue(bankStub.held);
        assertTrue(bankStub.posted);
        cardReaderStub.remove();
        assertFalse(readerControllerStub.isPaying);
        assertEquals(controllerStub.getRemainingAmount(), BigDecimal.valueOf(-1));
        assertTrue(cardReaderStub.isDisabled());
        readerControllerStub.card = null;
    }

    @Test
    public void payWithSwipe() {
        assertTrue(cardReaderStub.isDisabled());
        readerControllerStub.enablePayment(bankStub, cardStub, BigDecimal.ONE);
        assertFalse(cardReaderStub.isDisabled());
        readerControllerStub.card = cardStub;
        bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        try {
            cardReaderStub.swipe(cardStub, new BufferedImage(10, 10, 10));
        } catch (Exception ex) {
            fail("Exception incorrectly thrown");
        }
        assertTrue(bankStub.held);
        assertTrue(bankStub.posted);
        assertFalse(readerControllerStub.isPaying);
        assertEquals(controllerStub.getRemainingAmount(), BigDecimal.valueOf(-1));
        assertTrue(cardReaderStub.isDisabled());
        readerControllerStub.card = null;
    }

    @Test
    public void payWithDebitSwipe() {
        assertTrue(cardReaderStub.isDisabled());
        readerControllerStub.enablePayment(bankStub, debitStub, BigDecimal.ONE);
        assertFalse(cardReaderStub.isDisabled());
        readerControllerStub.card = debitStub;
        bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        try {
            cardReaderStub.swipe(debitStub, new BufferedImage(10, 10, 10));
        } catch (Exception ex) {
            fail("Exception incorrectly thrown");
        }
        assertTrue(bankStub.held);
        assertTrue(bankStub.posted);
        assertFalse(readerControllerStub.isPaying);
        assertEquals(controllerStub.getRemainingAmount(), BigDecimal.valueOf(-1));
        assertTrue(cardReaderStub.isDisabled());
        readerControllerStub.card = null;
    }

    @Test
    public void payWithGift() {
        assertTrue(cardReaderStub.isDisabled());
        readerControllerStub.enableGiftPayment(giftStub, BigDecimal.ONE);
        assertFalse(cardReaderStub.isDisabled());
        readerControllerStub.giftCard = giftStub;
        try {
            cardReaderStub.insert(giftStub, "1313");
        } catch (Exception ex) {
            fail("Exception incorrectly thrown");
        }
        assertEquals(controllerStub.getRemainingAmount(), BigDecimal.valueOf(-1));
        readerControllerStub.giftCard = null;
    }

    /*
        @Test
        public void testPayWithGiftException() {
            assertTrue(cardReaderStub.isDisabled());
            readerControllerStub.enableGiftPayment(giftStub, BigDecimal.valueOf(15));
            assertFalse(cardReaderStub.isDisabled());
            readerControllerStub.giftCard = giftStub;
            try {
                cardReaderStub.payByGiftCard(BigDecimal.valueOf(15), giftStub);
            } catch (Exception ex){
                fail("Exception incorrectly thrown");
            }
            assertEquals(controllerStub.getRemainingAmount(), BigDecimal.valueOf(10));
            readerControllerStub.giftCard = null;
        }
       */
    @Test
    public void payByGiftFull() {
        assertFalse(readerControllerStub.isPaying);
        controllerStub.cost = BigDecimal.ONE;
        controllerStub.payByGiftCard(BigDecimal.ONE, giftStub);
        assertFalse(cardReaderStub.isDisabled());
    }

    @After
    public void teardown() {
        bankStub = null;
        controllerStub = null;
        cardReaderStub = null;
        cardStub = null;
        readerControllerStub = null;
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