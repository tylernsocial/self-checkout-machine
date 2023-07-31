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

import com.autovend.devices.ElectronicScale;
import com.autovend.devices.observers.ElectronicScaleObserver;
import com.autovend.products.Product;

public class ElectronicScaleController extends BaggingAreaController<ElectronicScale, ElectronicScaleObserver>
        implements ElectronicScaleObserver {
    CheckoutController mainController = getMainController();
    private double currentWeight;
    private double expectedWeight;
    private boolean addingBags;
    private boolean AttendantApproval;

    public ElectronicScaleController(ElectronicScale newDevice) {
        super(newDevice);
    }

    /**
     * Method used to update the expected weight for validation of orders.
     *
     * @param nextProduct
     * @param weightInGrams
     */
    @Override
    void updateExpectedBaggingArea(Product nextProduct, double weightInGrams) {
        this.expectedWeight += weightInGrams;
        this.setBaggingValid(false);
        // TODO: Figure out how changes smaller than sensitivity would be handled
        // TODO: Also figure out how items which would cause the scale to be overloaded
        // should be handled.
    }

    @Override
    public void resetOrder() {
        this.setBaggingValid(true);
        this.currentWeight = 0;
        this.expectedWeight = 0;
    }

    public void attendantInput(boolean approval) {
        AttendantApproval = approval;
    }

    @Override
    public void reactToWeightChangedEvent(ElectronicScale scale, double weightInGrams) {
        if (scale != this.getDevice()) {
            return;
        }
        this.currentWeight = weightInGrams;
        // if the customer is adding their own bags, no need to check the expected
        // weight as there is not one yet
        if (addingBags) {
            return;
        }
        if (this.currentWeight == this.expectedWeight) {
            this.getMainController().baggedItemsValid(this);
        }
        // case of weight discrepancy
        else {


            // boolean value resolveDisrepancy:
            // true if discrepancy is resolved by:
            // -a do not bag request from customer IO
            // -attendant approval
            boolean resolveDiscrepancy = false;

            // system blocks checkout from further interaction
            this.getMainController().baggingItemLock = true;

            // discrepancy resolved if customer signals a dnb request or attendant approves
            if (AttendantApproval)
                resolveDiscrepancy = true;

            // validates bagging if the discrepancy was resolved
            if (resolveDiscrepancy) {
                this.getMainController().baggedItemsValid(this);
                this.getMainController().baggingItemLock = false;
            } else {
                this.getMainController().baggedItemsInvalid(this,
                        "The items in the bagging area don't have the correct weight.");
            }

        }
    }

    @Override
    public void reactToOverloadEvent(ElectronicScale scale) {
        if (scale != this.getDevice()) {
            return;
        }
        this.getMainController().baggingAreaError(this,
                "The scale is currently overloaded, please take items off it to avoid damaging the system.");
    }

    public final void doNotAddItemToBaggingArea(ElectronicScale scale, double weight) {
        //lock from adding more items
        this.getMainController().baggingItemLock = true;

        //TODO: System: Signals to the Attendant I/O that a no-bagging request is in progress.
        //TODO: Approve request (function created below)

        //if attendant approved, reduce expected weight
        if (this.getMainController().AttendantApproved) {
            for (BaggingAreaController baggingController : this.getMainController().getValidBaggingControllers()) {
                removeAddedWeight(weight);
            }
        }
        //unlock system when done so they can continue adding items
        this.getMainController().baggingItemLock = false;

    }

    @Override
    public void reactToOutOfOverloadEvent(ElectronicScale scale) {
        if (scale != this.getDevice()) {
            return;
        }
        this.getMainController().baggingAreaErrorEnded(this, "The scale is no longer overloaded.");
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    public void updateWithBagWeight(double weight) {
        this.expectedWeight += weight;
    }

    public double getExpectedWeight() {
        return this.expectedWeight;
    }

    public boolean getAddingBags() {
        return this.addingBags;
    }

    public void setAddingBags(boolean value) {
        this.addingBags = value;
    }

    public void removeAddedWeight(double weight) {
        this.expectedWeight -= weight;
    }

}
