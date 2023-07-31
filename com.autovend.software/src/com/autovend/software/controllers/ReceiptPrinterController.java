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

import com.autovend.devices.EmptyException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.ReceiptPrinter;
import com.autovend.devices.observers.ReceiptPrinterObserver;
import com.autovend.products.Product;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

public class ReceiptPrinterController extends DeviceController<ReceiptPrinter, ReceiptPrinterObserver>
        implements ReceiptPrinterObserver {
    // Flags/indicators that ink or paper levels are low
    public boolean inkLow = true;
    public boolean paperLow = true;
    public int estimatedInk = 0;
    public int estimatedPaper = 0;
    private CheckoutController mainController;
    private ReceiptPrinter printer;

    // How do we update the estimated ink and paper on refills? - Arie

    public ReceiptPrinterController(ReceiptPrinter newDevice) {
        super(newDevice);
    }

    public final CheckoutController getMainController() {
        return this.mainController;
    }

    public final void setMainController(CheckoutController newMainController) {
        if (this.mainController != null) {
            this.mainController.deregisterReceiptPrinter(this);
        }
        this.mainController = newMainController;
        if (this.mainController != null) {
            this.mainController.registerReceiptPrinter(this);
        }
    }

    /**
     * Function for software to keep track of how much ink printer has Since there
     * are no sensors, whenever ink is added to printer, its incremented in the
     * software using this function
     *
     * @param inkAmount: amount of ink added to printer
     */
    public void addedInk(int inkAmount) {
        if (inkAmount > 0)
            estimatedInk += inkAmount;
        else
            System.out.println("Negative Ink Not Allowed to be Added");

        inkLow = inkAmount <= 500;
    }

    /**
     * Function for software to keep track of how much paper printer has Since there
     * are no sensors, whenever paper is added to printer, its incremented in the
     * software using this function
     *
     * @param paperAmount: amount of paper added to printer
     */
    public void addedPaper(int paperAmount) {
        if (paperAmount > 0)
            estimatedPaper += paperAmount;
        else
            System.out.println("Negative Paper Not Allowed to be Added");

        paperLow = paperAmount <= 200;
    }

    /**
     * Responsible for printing out a properly formatted Receipt using the list of
     * Products and total cost. The receipt will contain a numbered list containing
     * the price of each product.
     *
     * @param order: HashMap of Products on the order
     * @param cost:  total cost of the order
     */
    public void printReceipt(LinkedHashMap<Product, Number[]> order, BigDecimal cost) {

        printer = getDevice();

        // initialize String Builder to build the receipt
        StringBuilder receipt = new StringBuilder();
        receipt.append("Purchase Details:\n");

        // loop through every product in the order, appending the appropriate strings to
        // the receipt
        int i = 1;

        for (Product product : order.keySet()) {
            Number[] productInfo = order.get(product);

            // We only need to focus on per-unit costs currently, weight-based will be
            // handled later.
            // if (product.isPerUnit()){
            // going through the string and splitting to avoid writing too much
            // on one line
            String productName = product.getClass().getSimpleName();
            String productString = String.format("%d $%.2f %dx %s\n", i, productInfo[1], productInfo[0].intValue(),
                    productName);
            int splitPos = 59;
            String splitterSubString = "-\n    -";
            while (splitPos < productString.length() - 1) {// -1 to not worry about the \n at the end.
                productString = productString.substring(0, splitPos) + splitterSubString
                        + productString.substring(splitPos);
                splitPos += 61;// 1 extra to account for \n being 1 character (prevents double-spacing of text)
            }
            // }
            receipt.append(productString);
            i++;
        }
        // append total cost at the end of the receipt
        receipt.append(String.format("Total: $%.2f\n", cost));

        try {
            for (char c : receipt.toString().toCharArray()) {
                if (c == '\n') {
                    estimatedPaper--;
                } else if (!Character.isWhitespace(c)) {
                    estimatedInk--;
                }

                printer.print(c);
            }
            printer.cutPaper();
        } catch (OverloadException e) {
            System.out.println("The receipt is too long.");
        } catch (EmptyException e) {
            System.out.println("The printer is out of paper or ink.");
            this.mainController.printerOutOfResources(this);
        }

        if (estimatedInk <= 500) {
            // Inform the I/O for attendant from the error message about low ink
            // this is a placeholder currently.
            System.out.println("Ink Low for Station: " + mainController.getID());
            inkLow = true;
        } else
            inkLow = false;
        if (estimatedPaper <= 200) {
            // Inform the I/O for attendant from the error message about low ink
            // this is a placeholder currently.
            System.out.println("Paper Low for Station: " + mainController.getID());
            paperLow = true;
        } else
            paperLow = false;
    }

    @Override
    public void reactToOutOfPaperEvent(ReceiptPrinter printer) {
        estimatedPaper = 0;
        this.mainController.printerOutOfResources(this);
    }

    @Override
    public void reactToOutOfInkEvent(ReceiptPrinter printer) {
        estimatedInk = 0;
        this.mainController.printerOutOfResources(this);
    }

    @Override
    public void reactToPaperAddedEvent(ReceiptPrinter printer) {
        this.mainController.printerRefilled(this);
    }

    @Override
    public void reactToInkAddedEvent(ReceiptPrinter printer) {
        this.mainController.printerRefilled(this);
    }

}
