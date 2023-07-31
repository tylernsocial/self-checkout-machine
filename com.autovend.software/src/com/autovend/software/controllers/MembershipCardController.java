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

import com.autovend.IllegalDigitException;

public class MembershipCardController {
    public String membershipNumber;
    private final boolean isActive = false;
    // Did a max tries of 3, having a limit would help with like not having a
    // infinite input that is invalid,
    // and after the three invalid attempts it will return null

    public boolean getIsActive() {
        return this.isActive;
    }

    /*
     * The isValid method will first check if memberNUM is null or if its length is
     * not equal to 12. If any of those conditions are true then it will through the
     * exception "IllegalDigitException" and say that it needs to be exactly 12
     * digits long. Then it will use the Character.isDigit to check each digit in
     * memberNum and see if there are any non-digits and that it is a digit between
     * 0-9. If there is a non-digit it will throw the "IllegalDigitException" saying
     * that it should be a digit between 0-9.
     */

    public boolean isValid(String memberNum) throws IllegalDigitException {
        if (memberNum == null || memberNum.length() != 12) {
            throw new IllegalDigitException("The Membership number should be exactly 12 digits long.");
        }
        for (int i = 0; i < memberNum.length(); i++) {
            char c = memberNum.charAt(i);
            if (!Character.isDigit(c)) {
                throw new IllegalDigitException("The Membership number should only contain digits between 0-9.");
            }
        }
        return true;
    }
}
