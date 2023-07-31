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

import com.autovend.Coin;
import com.autovend.devices.CoinDispenser;
import com.autovend.devices.EmptyException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.observers.CoinDispenserObserver;

import java.math.BigDecimal;

public class CoinDispenserController extends ChangeDispenserController<CoinDispenser, CoinDispenserObserver>
        implements CoinDispenserObserver {
    public CoinDispenserController(CoinDispenser newDevice, BigDecimal denom) {
        super(newDevice, denom);
    }

    @Override
    public void reactToCoinsFullEvent(CoinDispenser dispenser) {
    }

    @Override
    public void reactToCoinsEmptyEvent(CoinDispenser dispenser) {
    }

    @Override
    public void reactToCoinAddedEvent(CoinDispenser dispenser, Coin coin) {
    }

    @Override
    public void reactToCoinRemovedEvent(CoinDispenser dispenser, Coin coin) {
    }

    @Override
    public void reactToCoinsLoadedEvent(CoinDispenser dispenser, Coin... coins) {
    }

    @Override
    public void reactToCoinsUnloadedEvent(CoinDispenser dispenser, Coin... coins) {
    }

    @Override
    void emitChange() {
        try {
            this.getDevice().emit();
        } catch (EmptyException ex) {
            this.getMainController().changeDispenseFailed(this, this.getDenom());
        } catch (OverloadException ex) {
            System.out.println("This can't physically happen, something went wrong.");
        }
    }
}
