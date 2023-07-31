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

import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SimulationException;
import com.autovend.software.controllers.CheckoutController;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import static org.junit.Assert.*;

//Contains 

public class AttendantIDTest {
    Currency c = Currency.getInstance(Locale.CANADA);
    int[] BillDenomiantions = {5, 10, 20, 50, 100};
    BigDecimal[] coinDenominations = {new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2")};


    SelfCheckoutStation station = new SelfCheckoutStation(c, BillDenomiantions, coinDenominations, 1000000, 1);

    /**
     * Log In Test
     * Test 1: Trying to Log in with Wrong Credentials
     * Test 2: Successful Login after Adding a new Attendant
     * Test 3: Check what happens when someone tries to log in again when another attendant is logged in
     * Test 4: Wrong Password for a User ID
     * Test 5: Successful Login for super attendant
     * Test 6: Test Remove Attendant Function
     **/


    // Test 1: Trying to Log in with Wrong Credentials
    @Test(expected = SimulationException.class)
    public void Failed_Log_in_WrongCreds() {
        // Creating Station Instance for Test 1
        CheckoutController stationTestLogin = new CheckoutController(station);

        // Calling Log in
        stationTestLogin.Log_in_Attendant("Tom", "3523rq12ui3r");

    }

    // Test 2: Successful Login after Adding a new Attendant
    @Test
    public void AddAttendtant_Succesful_Login() {
        // Creating Station Instance for Test 1
        CheckoutController stationTestLogin = new CheckoutController(station);
        // Adding New Attendant
        stationTestLogin.Attendant.add_attendant("Samuel", "oper14xj65");
        // Calling Log in
        stationTestLogin.Log_in_Attendant("Samuel", "oper14xj65");
        //Check Log_in_Status
        assertTrue(stationTestLogin.Log_in_Status);

    }


    // Test 3: Trying to Log in Multiple Times
    @Test(expected = SimulationException.class)
    public void Overlapping_Log_in() {
        // Creating Station Instance for Test 1
        CheckoutController stationTestLogin = new CheckoutController(station);
        // Adding New Attendant
        stationTestLogin.Attendant.add_attendant("Samuel", "oper14xj65");
        stationTestLogin.Attendant.add_attendant("Babar", "52618");

        // Calling Log in
        stationTestLogin.Log_in_Attendant("Samuel", "oper14xj65");
        stationTestLogin.Log_in_Attendant("Babar", "52618");


    }

    // Test 4: Real UserID with wrong password
    @Test(expected = SimulationException.class)
    public void Failed_Log_in_WrongPassword() {
        // Creating Station Instance for Test 1
        CheckoutController stationTestLogin = new CheckoutController(station);
        // Adding New Attendant
        // Calling Log in
        // inputting wrong password for James
        stationTestLogin.Log_in_Attendant("James", "331");
    }

    // Test 5: Real UserID with wrong password
    @Test
    public void Successful_Log_in_CurrentAttendant() {
        // Creating Station Instance for Test 1
        CheckoutController stationTestLogin = new CheckoutController(station);
        // Adding New Attendant
        // Log in Attendant
        stationTestLogin.Log_in_Attendant("James", "4628");
        assertTrue(stationTestLogin.Log_in_Status);
    }

    // Test 6: Test Remove Attendant Method
    @Test(expected = SimulationException.class)
    public void Remove_CurrentAttendant_FailLogin() {
        // Creating Station Instance for Test 1
        CheckoutController stationTestLogin = new CheckoutController(station);
        // Adding New Attendant
        // Log in Attendant
        stationTestLogin.Attendant.remove_attendant("James", "4628");
        stationTestLogin.Log_in_Attendant("James", "4628");
    }


    /**
     * Log Out Test
     * Test 1: No accounts are logged in
     * Test 2: Successful Logout ---> Checks if Attendant ID is null
     * Test 3: Successful Logout ----> Checks if Login Status is false
     **/

    //No accounts are logged in
    @Test(expected = SimulationException.class)
    public void Failed_Log_Out() {
        CheckoutController stationLogOut = new CheckoutController(station);
        // Calling Log out when no one is logged in
        stationLogOut.Log_Out_Attendant();

    }

    // Checks if the current attendant is null after logging out
    @Test
    public void Succesful_Log_Out_2() {
        CheckoutController stationLogOut = new CheckoutController(station);

        // Logged in as James [Super Attendant]
        stationLogOut.Log_in_Attendant("James", "4628");
        // Calling Log out when no one is logged in
        stationLogOut.Log_Out_Attendant();
        assertNull(stationLogOut.Attendant_ID);
    }

    //Successful Log Out
    @Test
    public void Succesfull_Log_Out() {
        CheckoutController stationLogOut = new CheckoutController(station);
        // Adding New Attendant
        stationLogOut.Attendant.add_attendant("Babar", "52618");
        // Calling Log out when no one is logged in
        stationLogOut.Log_in_Attendant("Babar", "52618");
        //Logging Out
        stationLogOut.Log_Out_Attendant();
        assertFalse(stationLogOut.Log_in_Status);

    }


}
