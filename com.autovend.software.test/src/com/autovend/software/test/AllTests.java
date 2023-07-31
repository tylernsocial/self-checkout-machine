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

import com.autovend.software.controllers.AddByBrowseItemController;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({AddItemTest.class, AddOwnBagsTest.class, LowInkPaperTest.class, PurchaseBagsTest.class,
        TestPrintReceipt.class, CardPaymentTest.class, TestMembershipCardController.class, AddByBrowseItemController.class,
        AddItemByTextSearchTest.class, AttendantIDTest.class, BillDispenserControllerTest.class, BillPaymentControllerTest.class, StartupShutdownTest.class,
        SuspendControllerTest.class, AddByBrowseItemControllerTest.class})
public class AllTests {
}
