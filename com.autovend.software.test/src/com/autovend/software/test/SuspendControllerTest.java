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

import com.autovend.software.controllers.SuspendController;
import com.autovend.software.controllers.SuspendController.Station;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SuspendControllerTest {

    private SuspendController suspendController;

    /**
     * Set up work station and suspendController object
     */
    @Before
    public void setUpTests() {
        suspendController = new SuspendController(null);
        Station station = suspendController.new Station("Test Station", false);
        suspendController = new SuspendController(station);
        suspendController.stationSetUp();
    }

    /**
     * Test if the method stationSeetUp() actually sets the field suspended to true
     * Expected: public field suspended should be true after calling this method
     */
    @Test
    public void testStationSetUp() {
        assertTrue(suspendController.isSuspended());
    }

    // Testing when station is not in used by any customer and is not currently suspended
    @Test
    public void testSuspend() {
        SuspendController suspendController = new SuspendController(null);
        Station testStation = suspendController.new Station("Test Station", false);
        testStation.setInUse(false);
        assertEquals("Test Station", testStation.getName());
        suspendController = new SuspendController(testStation);
        assertEquals(false, testStation.isInUse());
        suspendController.suspend();
        assertTrue(suspendController.isSuspended());
    }

    //Trying to test if the station is already suspended.
    @Test
    public void testAlreadySuspend() {
        SuspendController suspendController = new SuspendController(null);
        Station testStation = suspendController.new Station("Test Station", false);
        suspendController = new SuspendController(testStation);
        suspendController.suspended = true;
        suspendController.suspend();
        assertFalse(suspendController.isSuspended());
    }

    //Testing when the station is not suspened and is in used by customer.
    @Test
    public void testSuspendCustomerInUse() {
        SuspendController suspendController = new SuspendController(null);
        Station testStation = suspendController.new Station("Test Station", true);
        suspendController = new SuspendController(testStation);
        suspendController.suspend();
        assertFalse(suspendController.isSuspended());
    }

    /**
     * Test if the method unSuspend() sets the boolean value suspended to false if it is currently true
     * Expected: public field suspended should be false after calling this method
     */
    @Test
    public void unSuspend_Suspended_Test() {
        suspendController.unsuspend();
        assertFalse(suspendController.isSuspended());
    }

    @Test
    public void testUnsuspend() {
        suspendController.suspended = false;
        suspendController.unsuspend();
        assertFalse(suspendController.isSuspended());
    }
}
