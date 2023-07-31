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

import com.autovend.Barcode;
import com.autovend.devices.BarcodeScanner;
import com.autovend.devices.observers.BarcodeScannerObserver;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;

/**
 * Controller for the barcode scanner, communicates with main checkout
 * controller to add items to order.
 */
public class BarcodeScannerController extends ItemAdderController<BarcodeScanner, BarcodeScannerObserver>
        implements BarcodeScannerObserver {

    public BarcodeScannerController(BarcodeScanner scanner) {
        super(scanner);
    }

    public void reactToBarcodeScannedEvent(BarcodeScanner barcodeScanner, Barcode barcode) {

        // if barcode is for a valid object, then add the product found to the order on
        // the main controller.
        // otherwise ignore the item.
        if (barcodeScanner != this.getDevice()) {
            return;
        }
        if (this.getMainController().inputMembership) {
            if (!this.getMainController().existedMembership && this.getMainController().membershipCardController.isValid(barcode.toString())) {
                this.getMainController().membershipNum = barcode.toString();
//			System.out.println(this.getMainController().membershipNum);
                this.getMainController().existedMembership = true;
                this.getMainController().inputMembership = false;
                return;
            }
        }


        BarcodedProduct scannedItem = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
        if (scannedItem != null) {
            this.getMainController().addItem(this, scannedItem, scannedItem.getExpectedWeight());
        }
    }
}
