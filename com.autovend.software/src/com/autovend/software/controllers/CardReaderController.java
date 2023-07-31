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

import com.autovend.*;
import com.autovend.Card.CardData;
import com.autovend.GiftCard.GiftCardInsertData;
import com.autovend.devices.CardReader;
import com.autovend.devices.observers.CardReaderObserver;
import com.autovend.external.CardIssuer;

import java.math.BigDecimal;


public class CardReaderController extends PaymentController<CardReader, CardReaderObserver>
        implements CardReaderObserver {
    public boolean isPaying;
    public boolean insertPayment;
    public boolean tapPayment;
    public boolean swipePayment;
    public Card card;
    public CreditCard creditCard;
    public DebitCard debitCard;
    public GiftCardInsertData giftData;
    public CardData data;
    public CardIssuer bank;
    public boolean paymentFailure;
    public boolean creditPayment = false;
    public boolean debitPayment = false;
    public boolean giftPayment = false;
    public boolean bTap = false;
    public boolean bSwipe = false;
    public boolean bInsert = false;
    public boolean bcredit = false;
    public boolean bdebit = false;
    public GiftCard giftCard;
    public CardData cardData;
    String userPin;
    private BigDecimal amount;

    public CardReaderController(CardReader newDevice) {
        super(newDevice);
    }


    /**
     * This is a method that takes care of the bank side of the payment use case. Originally a part of reactToCardDataReadEvent, moved here to support other parts of the use case (tap/swipe). Credit Version
     *
     * @param localdata Local card data, given from other payment methods.
     * @param localbank Bank data, should also have been given from detected credit card.
     * @throws BlockedCardException If something goes wrong with the transaction.
     */
    public void bankCreditPayment(CardData localdata, CardIssuer localbank) throws BlockedCardException {
        int holdNum = localbank.authorizeHold(localdata.getNumber(), this.amount);
        if (holdNum != -1 && (localbank.postTransaction(localdata.getNumber(), holdNum, this.amount))) {
            getMainController().addToAmountPaid(this.amount);
        } else {
            throw new BlockedCardException();
        }
    }

    /**
     * This is a method that takes care of the bank side of the payment use case. Originally a part of reactToCardDataReadEvent, moved here to support other parts of the use case (tap/swipe). Debit Version.
     *
     * @param localdata Local card data, given from other payment methods.
     * @param localbank Bank data, should also have been given from detected credit card.
     * @throws BlockedCardException If something goes wrong with the transaction.
     */
    public void bankDebitPayment(CardData localdata, CardIssuer localbank) throws BlockedCardException {
        int holdNum = localbank.authorizeHold(localdata.getNumber(), this.amount);
        if (holdNum != -1 && (localbank.postTransaction(localdata.getNumber(), holdNum, this.amount))) {
            getMainController().addToAmountPaid(this.amount);
        } else {
            throw new BlockedCardException();
        }
    }

    /**
     * This method handles the main system side of the payment for gift cards.
     *
     * @param localdata Local card data, given from other payment methods.
     * @throws ChipFailureException In case the gift card data can't be read, thrown.
     */
    public void giftPayment(GiftCardInsertData localdata) throws ChipFailureException {
        BigDecimal balance = localdata.getRemainingBalance();
        if (balance.compareTo(amount) == 1 || balance.compareTo(amount) == 0) {
            localdata.deduct(amount);
            getMainController().addToAmountPaid(this.amount);
        } else {
            throw new ChipFailureException();
        }
    }

    /**
     * This method handles a tap credit payment.
     *
     * @param card The card tapped against the card reader.
     * @param data Data from the tapped card.
     * @throws TapFailureException  If the tap fails, throw exception. Will likely communicate something when GUI is up.
     * @throws BlockedCardException If the card is rejected by the bank, thrown.
     */
    public void tapCreditPayment(CreditCard localCard, CardData localData) throws TapFailureException, BlockedCardException {
        if (!localCard.hasChip || !localCard.isTapEnabled) {
            throw new TapFailureException();
        } else {
            bankCreditPayment(localData, this.bank);
        }
    }

    /**
     * This method handles a tap debit payment.
     *
     * @param card The card tapped against the card reader.
     * @param data Data from the tapped card.
     * @throws TapFailureException  If the tap fails, throw exception. Will likely communicate something when GUI is up.
     * @throws BlockedCardException If the card is rejected by the bank, thrown.
     */
    public void tapDebitPayment(DebitCard localCard, CardData localData) throws TapFailureException, BlockedCardException {
        if (!localCard.hasChip || !localCard.isTapEnabled) {
            throw new TapFailureException();
        } else {
            bankDebitPayment(localData, this.bank);
        }
    }

    /**
     * This method handles when a credit card is inserted into the card reader.
     *
     * @param card The card inserted into the card reader.
     * @param data Data from the inserted card.
     * @throws ChipFailureException If the chip isn't read properly, throw exception.
     * @throws BlockedCardException If the card is rejected by the bank, thrown.
     */
    public void insertCreditPayment(CreditCard localCard, CardData localData) throws ChipFailureException, BlockedCardException {
        if (!localCard.hasChip) {
            throw new ChipFailureException();
        } else {
            bankCreditPayment(localData, this.bank);
        }
    }


    /**
     * This method handles when a debit card is inserted into the card reader.
     *
     * @param localCard The card inserted into the card reader.
     * @param data      Data from the inserted card.
     * @throws ChipFailureException If the chip isn't read properly, throw exception.
     * @throws BlockedCardException If the card is rejected by the bank, thrown.
     */
    public void insertDebitPayment(DebitCard localCard, CardData localData) throws ChipFailureException, BlockedCardException {
        if (!localCard.hasChip) {
            throw new ChipFailureException();
        } else {
            bankDebitPayment(localData, this.bank);
        }
    }

    /**
     * This method handles when a gift card is inserted into the card reader.
     *
     * @param localCard The gift card involved in the transaction.
     * @param localData The data of the gift card.
     * @throws ChipFailureException If the data on the gift card can't be read, thrown.
     */
    public void insertGiftPayment(GiftCard localCard, GiftCardInsertData localData) throws ChipFailureException {
        giftPayment(localData);
    }

    /**
     * This method handles when a credit card is swiped against the card reader.
     *
     * @param localCard The card inserted into the card reader.
     * @param localData Data from the inserted card.
     * @throws BlockedCardException If the card is rejected by the bank, thrown.
     */
    public void swipeCreditPayment(Card localCard, CardData localData) throws BlockedCardException {
        bankCreditPayment(localData, this.bank);
    }

    /**
     * This method handles when a debit card is swiped against the card reader.
     *
     * @param localCard The card inserted into the card reader.
     * @param localData Data from the inserted card.
     * @throws BlockedCardException If the card is rejected by the bank, thrown.
     */
    public void swipeDebitPayment(DebitCard localCard, CardData localData) throws BlockedCardException {
        bankDebitPayment(localData, this.bank);
    }

    @Override
    public void reactToCardInsertedEvent(CardReader reader) {
        //Sets internal flag for insert payment.
        this.isPaying = true;
        this.insertPayment = true;
        bInsert = true;
    }

    @Override
    public void reactToCardRemovedEvent(CardReader reader) {
        this.isPaying = false;
    }

    @Override
    public void reactToCardTappedEvent(CardReader reader) {
        //Sets internal flag for tap payment.
        this.isPaying = true;
        this.tapPayment = true;
        bTap = true;
    }

    @Override
    public void reactToCardSwipedEvent(CardReader reader) {
        //Sets internal flag for swipe payment.
        this.isPaying = true;
        this.swipePayment = true;
        bSwipe = true;
    }

    @Override
    public void reactToCardDataReadEvent(CardReader reader, Card.CardData data) {

        if (this.getMainController().inputMembership) {
            if (data.getType().equals("Membership")) {
                if (!this.getMainController().existedMembership && this.getMainController().membershipCardController.isValid(data.getNumber())) {
                    this.getMainController().membershipNum = data.getNumber();
                    this.getMainController().existedMembership = true;
                    this.getMainController().inputMembership = false;
                    return;
                }
            }
        }
        if (data.getType().equals("Credit Card")) {
            this.creditCard = (CreditCard) this.card;
            creditPayment = true;
            bcredit = true;
        }
        if (data.getType().equals("Debit Card")) {
            this.debitCard = (DebitCard) this.card;
            debitPayment = true;
            bdebit = true;
        }
        this.isPaying = true;
        //Data is harvested from the card and saved to the reader.
        this.data = data;
        if (giftPayment && this.insertPayment) {
            giftData = (GiftCardInsertData) data;
            try {
                insertGiftPayment(this.giftCard, giftData);
            } catch (ChipFailureException e) {
                // This will likely jump back to another method once GUI is set up, possible second payment attempt?
                paymentFailure = true;
            }
        }
        if (reader != this.getDevice() || !this.isPaying || this.bank == null) {
            return;
        }
        //One of these three flags should have been set before this event happens.
        //Tap payment, set during cardTappedEvent.
        if (this.tapPayment && this.creditPayment) {
            try {
                tapCreditPayment(this.creditCard, this.data);
            } catch (TapFailureException e) {
                // This will likely jump back to another method once GUI is set up, possible second payment attempt?
                paymentFailure = true;
            } catch (BlockedCardException e) {
                // This will likely jump back to another method once GUI is set up, possible second payment attempt?
                paymentFailure = true;
                return;
            }
        }
        if (this.tapPayment && this.debitPayment) {
            try {
                tapDebitPayment(this.debitCard, this.data);
            } catch (TapFailureException e) {
                // This will likely jump back to another method once GUI is set up, possible second payment attempt?
                paymentFailure = true;
            } catch (BlockedCardException e) {
                // This will likely jump back to another method once GUI is set up, possible second payment attempt?
                paymentFailure = true;
                return;
            }
        }
        //Insert payment, set during cardInsertedEvent.
        if (this.insertPayment && this.creditPayment) {
            try {
                insertCreditPayment(this.creditCard, this.data);
            } catch (ChipFailureException | BlockedCardException e) {
                // This will likely jump back to another method once GUI is set up, possible second payment attempt?
                paymentFailure = true;
                return;
            }
        }
        if (this.insertPayment && this.debitPayment) {
            try {
                insertDebitPayment(this.debitCard, this.data);
            } catch (ChipFailureException | BlockedCardException e) {
                // This will likely jump back to another method once GUI is set up, possible second payment attempt?
                paymentFailure = true;
                return;
            }
        }
        //Swipe payment, set during cardSwipedEvent
        if (this.swipePayment && this.creditPayment) {
            try {
                swipeCreditPayment(this.creditCard, this.data);
            } catch (BlockedCardException e) {
                // This will likely jump back to another method once GUI is set up, possible second payment attempt?
                paymentFailure = true;
            }
        }
        if (this.swipePayment && this.debitPayment) {
            try {
                swipeDebitPayment(this.debitCard, this.data);
            } catch (BlockedCardException e) {
                // This will likely jump back to another method once GUI is set up, possible second payment attempt?
                paymentFailure = true;
            }
        }
        this.isPaying = false;
        this.insertPayment = false;
        this.swipePayment = false;
        this.tapPayment = false;
        this.disableDevice();
        this.bank = null;
        this.giftPayment = false;
        this.creditPayment = false;
        this.debitPayment = false;
    }

    /**
     * This activates the card reader for payment.
     *
     * @param issuer Bank that issued the card
     * @param amount Amount to be paid.
     */
    public void enablePayment(CardIssuer issuer, Card localCard, BigDecimal amount) {
        this.enableDevice();
        this.card = localCard;
        this.bank = issuer;
        this.amount = amount;
    }

    /**
     * This activates the card reader for a gift card payment.
     *
     * @param localGift Gift card to be used for the transaction.
     * @param amount    Amount to be paid.
     */
    public void enableGiftPayment(GiftCard localGift, BigDecimal amount) {
        this.enableDevice();
        this.giftCard = localGift;
        this.amount = amount;
        this.giftPayment = true;
    }

    public BigDecimal getAmountDue() {
        return this.amount;
    }

    public BigDecimal getAmountPaid() {
        return getMainController().amountPaid;
    }

    public String getPaymentType() {
        if (bcredit) {
            return "Credit Card";
        }
        if (bdebit) {
            return "Debit Card";
        }
        return null;
    }

    public String tapInput() {
        if (bTap) {
            return "TAP";
        }
        return null;
    }

    public String insertInput() {
        if (bInsert) {
            return "INSERT";
        }
        return null;
    }

    public String swipeInput() {
        if (bSwipe) {
            return "SWIPE";
        }
        return null;
    }
}