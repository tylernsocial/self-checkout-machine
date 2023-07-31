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

import com.autovend.devices.SelfCheckoutStation;

import java.util.HashMap;

public class AttendantController {
    // Handles the Operations of the attendant

    // Attendant user ID
    public String user_id;
    // Attendant Password
    public String password;

    // Attendant Lists for the Software
    public HashMap<String, String> AttendantList = new HashMap<String, String>();

    /**
     * Constructs an Attendant with user ID and Password
     *
     * @param username
     * @param key
     */
    public AttendantController(String username, String pass) {
        this.user_id = username;
        this.password = pass;
        // Adding Some Current Attendants to the List
        AttendantList.put("James", "4628");
        AttendantList.put("Wayne", "331");
        AttendantList.put("Shaw", "unux89");
        AttendantList.put("Pablo", "12345678");
    }

    /**
     * Adds the userID and password for a new attendant
     **/
    public void add_attendant(String username, String password) {

        AttendantList.put(username, password);

    }

    /**
     * Method to remove attendant
     **/

    public void remove_attendant(String username, String pass) {

        AttendantList.remove(username, pass);
    }

    // Getter method for Attendant ID
    public String getUser_id() {
        return user_id;
    }


    public void attendantStationStartup(CheckoutController checkoutController, SelfCheckoutStation scs) {
        checkoutController.stationStartup(scs);
    }

    public void attendantStationShutdown(CheckoutController checkoutController, SelfCheckoutStation scs) {
        checkoutController.stationShutdown(scs);
    }

}
