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
import com.autovend.devices.*;
import com.autovend.external.CardIssuer;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static com.autovend.external.ProductDatabases.BARCODED_PRODUCT_DATABASE;
import static com.autovend.external.ProductDatabases.PLU_PRODUCT_DATABASE;

@SuppressWarnings("rawtypes")

public class CheckoutController {
    private static int IDcounter = 1;
    // sets of valid sources of information to the main controller.
    private final HashSet<BaggingAreaController> validBaggingControllers;
    private final HashSet<ItemAdderController> validItemAdderControllers;
    private final LinkedHashSet<ChangeSlotController> changeSlotControllers;
    public BigDecimal cost;
    // Flag to prevent further addition of items if waiting to bag item or an
    // invalid item was found in the bagging area.
    public boolean baggingItemLock;
    // Flag to lock processing in case of damage to station
    // Specifically bagging area for this case, but could be used elsewhere if
    // needed.
    public boolean systemProtectionLock;
    /*
     * Boolean that indicates if an attendant has approved a certain action
     */
    public boolean AttendantApproved = false;
    // Tells the system about the current attendant
    public AttendantController Attendant;
    // Tells the system if an attendant is logged in
    public boolean Log_in_Status;
    // String to Display the name of current Attendant in charge
    public String Attendant_ID;
    public MembershipCardController membershipCardController = new MembershipCardController();
    public String membershipNum = "";
    public boolean existedMembership = false;
    public boolean inputMembership = false;
    public boolean needPrinterRefill = false;
    protected BigDecimal amountPaid;
    private final int stationID = IDcounter++;
    private LinkedHashMap<Product, Number[]> order;
    private final HashSet<PaymentController> validPaymentControllers;
    private ReceiptPrinterController receiptPrinter;
    private final TreeMap<BigDecimal, ChangeDispenserController> changeDispenserControllers;
    private boolean payingChangeLock;
    // create map to store current weight in bagging area
    private final Map<BaggingAreaController, Double> weight = new HashMap<>();
    // create map to store weight after bags added in bagging area
    private final Map<BaggingAreaController, Double> weightWithBags = new HashMap<>();
    private final Map<String, Integer> payCardAttempts = new HashMap<>();
    private CardReaderController cardReaderController;

    /**
     * Constructors for CheckoutController
     */

    public CheckoutController() {
        validBaggingControllers = new HashSet<>();
        validItemAdderControllers = new HashSet<>();
        validPaymentControllers = new HashSet<>();
        receiptPrinter = null;
        this.changeDispenserControllers = new TreeMap<>();
        this.changeSlotControllers = new LinkedHashSet<>();
        clearOrder();
        this.Log_in_Status = false;
    }

    public CheckoutController(SelfCheckoutStation checkout) {
        BarcodeScannerController mainScannerController = new BarcodeScannerController(checkout.mainScanner);
        BarcodeScannerController handheldScannerController = new BarcodeScannerController(checkout.handheldScanner);
        this.validItemAdderControllers = new HashSet<>(Arrays.asList(mainScannerController, handheldScannerController));

        ElectronicScaleController scaleController = new ElectronicScaleController(checkout.baggingArea);
        this.validBaggingControllers = new HashSet<>(List.of(scaleController));

        this.receiptPrinter = new ReceiptPrinterController(checkout.printer);

        BillPaymentController billPayController = new BillPaymentController(checkout.billValidator);
        CoinPaymentController coinPaymentController = new CoinPaymentController(checkout.coinValidator);
        this.cardReaderController = new CardReaderController(checkout.cardReader);

        this.validPaymentControllers = new HashSet<>(
                List.of(billPayController, coinPaymentController, cardReaderController));

        BillChangeSlotController billChangeSlotController = new BillChangeSlotController(checkout.billOutput);
        CoinTrayController coinChangeSlotController = new CoinTrayController(checkout.coinTray);

        // TODO: Finish Coin Tray Controller and add to controllers set
        this.changeSlotControllers = new LinkedHashSet<>(List.of(billChangeSlotController, coinChangeSlotController));
        this.changeDispenserControllers = new TreeMap<>();

        // Attendant
        // Tells the system if an attendant is logged in
        Log_in_Status = false;
        // String to Display the name of current Attendant in charge
        Attendant_ID = null;
        Attendant = new AttendantController("Tom", "6234523");


        // TODO: Also add coin dispensers to changeDispenserControllers (once done)

        for (int denom : checkout.billDispensers.keySet()) {
            changeDispenserControllers.put(BigDecimal.valueOf(denom),
                    new BillDispenserController(checkout.billDispensers.get(denom), BigDecimal.valueOf(denom)));
        }
        for (BigDecimal denom : checkout.coinDispensers.keySet()) {
            changeDispenserControllers.put(denom,
                    new CoinDispenserController(checkout.coinDispensers.get(denom), denom) {
                    });
        }
        // Add additional device peripherals for Customer I/O and Attendant I/O here
        registerAll();
        clearOrder();
    }

    public int getID() {
        return stationID;
    }

    /**
     * Method for clearing the current order, to be used for testing purposes,
     * resetting our order after payment, and to simplify our constructor code as
     * well.
     */
    void clearOrder() {
        // garbage collection will throw away the old objects, so implementing this way
        // lets us re-use this for
        // our constructor as well.
        this.order = new LinkedHashMap<>();
        this.cost = BigDecimal.ZERO;
        this.amountPaid = BigDecimal.ZERO;
        this.baggingItemLock = false;
        systemProtectionLock = false; // If the order is cleared, then nothing is at risk of damaging the station.
        payingChangeLock = false;
        for (BaggingAreaController controller : this.validBaggingControllers) {
            controller.resetOrder();
        }
    }

    // Getters for the order and cost for this checkout controller's current order.
    public HashMap<Product, Number[]> getOrder() {
        return this.order;
    }

    public void setOrder(LinkedHashMap<Product, Number[]> newOrd) {
        this.order = newOrd;

        for (Map.Entry<Product, Number[]> entry : this.order.entrySet()) {
            Product product = entry.getKey();
            this.cost = this.cost.add(product.getPrice());
        }
    }

    public BigDecimal getCost() {
        return this.cost;
    }

    /**
     * Methods to register and deregister peripherals for controlling the bagging
     * area and scanning and printer and methods of payment.
     */

    void registerBaggingAreaController(BaggingAreaController controller) {
        if (validBaggingControllers.contains(controller)) {
            return;
        }
        this.validBaggingControllers.add(controller);
    }

    void deregisterBaggingAreaController(BaggingAreaController controller) {
        if (!validBaggingControllers.contains(controller)) {
            return;
        }
        this.validBaggingControllers.remove(controller);
    }

    void registerItemAdderController(ItemAdderController adder) {
        if (validItemAdderControllers.contains(adder)) {
            return;
        }
        this.validItemAdderControllers.add(adder);
    }

    void deregisterItemAdderController(ItemAdderController adder) {
        if (!validItemAdderControllers.contains(adder)) {
            return;
        }
        this.validItemAdderControllers.remove(adder);
    }

    public void registerPaymentController(PaymentController controller) {
        if (validPaymentControllers.contains(controller)) {
            return;
        }
        this.validPaymentControllers.add(controller);
    }

    void deregisterPaymentController(PaymentController controller) {
        if (!validPaymentControllers.contains(controller)) {
            return;
        }
        this.validPaymentControllers.remove(controller);
    }

    public void registerReceiptPrinter(ReceiptPrinterController printer) {
        if (receiptPrinter == null) {
            this.receiptPrinter = printer;
        }
    }

    void deregisterReceiptPrinter(ReceiptPrinterController printer) {
        if (receiptPrinter.equals(printer)) {
            this.receiptPrinter = null;
        }
    }

    void registerChangeSlotController(ChangeSlotController controller) {
        this.changeSlotControllers.add(controller);
    }

    void deregisterChangeSlotController(ChangeSlotController controller) {
        this.changeSlotControllers.remove(controller);
    }

    void registerChangeDispenserController(BigDecimal denom, ChangeDispenserController controller) {
        if (!this.changeDispenserControllers.containsValue(controller)
                && !changeDispenserControllers.containsKey(denom)) {
            this.changeDispenserControllers.put(denom, controller);
        }
    }

    void deregisterChangeDispenserController(BigDecimal denom, ChangeDispenserController controller) {
        if (changeDispenserControllers.containsValue(controller) && changeDispenserControllers.containsKey(denom)) {
            this.changeDispenserControllers.remove(denom, controller);
        }
    }

    void registerAll() {
        for (ItemAdderController controller : validItemAdderControllers) {
            controller.setMainController(this);
        }
        for (BaggingAreaController controller : validBaggingControllers) {
            controller.setMainController(this);
        }
        for (PaymentController controller : validPaymentControllers) {
            controller.setMainController(this);
        }
        receiptPrinter.setMainController(this);
        for (ChangeSlotController controller : changeSlotControllers) {
            controller.setMainController(this);
        }
        for (BigDecimal denom : changeDispenserControllers.keySet()) {
            changeDispenserControllers.get(denom).setMainController(this);
        }
    }

    /**
     * Methods to get the device controllers for peripherals TODO: Add other methods
     * for payment controllers to these methods
     */
    public HashSet<BaggingAreaController> getAllBaggingControllers() {
        return validBaggingControllers;
    }

    public HashSet<ItemAdderController> getAllItemAdders() {
        return this.validItemAdderControllers;
    }

    HashSet<PaymentController> getAllPaymentControllers() {
        return this.validPaymentControllers;
    }

    ReceiptPrinterController getReceiptPrinter() {
        return this.receiptPrinter;
    }

    HashSet<ChangeSlotController> getChangeSlotController() {
        return this.changeSlotControllers;
    }

    public HashSet<DeviceController> getAllDeviceControllers() {
        HashSet<DeviceController> newSet = new HashSet<>(this.validItemAdderControllers);
        newSet.addAll(this.validBaggingControllers);
        newSet.addAll(this.validPaymentControllers);
        newSet.add(this.receiptPrinter);
        newSet.addAll(this.changeSlotControllers);
        newSet.addAll(this.changeDispenserControllers.values());
        newSet.remove(null);
        return newSet;
    }

    /*
     * Methods used by ItemAdderControllers
     */

    /**
     * A method to get the number of bags from the customer response
     *
     * @return number of bags
     */
    public int getBagNumber() {
        // Asking the customer to give the number of bags
        @SuppressWarnings("resource")
        Scanner scan = new Scanner(System.in);
        System.out.println("Number of bags to purchase?");
        String response = scan.nextLine();

        // If customer gives 0 then return
        if (response.equals("0")) {
            System.out.println("No bags added!");
            return 0;
        } else {
            // Otherwise record the customer response
            int bagNumber = Integer.parseInt(response);
            return bagNumber;
        }

    }

    /**
     * Method to add reusable bags to the order after the customer signals to buy
     * bags TODO: Implement the bags being dispensed by the bag dispenser
     *
     * @param adder   The ItemAdderController used to add the bag to the order
     * @param newBag  The product to be added to the current order
     * @param weight  The weight of the product to update the weight in the bagging
     *                area
     * @param numBags The number of bags getting added
     */
    public void purchaseBags(ItemAdderController adder, Product newBag, double weight, int numBags) {

        if ((!this.validItemAdderControllers.contains(adder)) || newBag == null) {
            return;
        }
        if (weight <= 0) {
            return;
        }
        if (baggingItemLock || systemProtectionLock) {
            return;
        }
        // If customer gives 0 then return
        if (numBags == 0) {
            System.out.println("No bags added!");
            return;
        }

        // Add the cost of the new bag to the current cost.
        BigDecimal bagCost = newBag.getPrice().multiply(BigDecimal.valueOf(numBags));
        this.cost = this.cost.add(bagCost);

        // Putting the bag information to the order
        Number[] currentBagInfo = new Number[]{numBags, bagCost};
        if (this.order.containsKey(newBag)) {
            Number[] existingBagInfo = this.order.get(newBag);
            int totalNumBags = existingBagInfo[0].intValue() + numBags;
            BigDecimal totalBagCost = ((BigDecimal) existingBagInfo[1]).add(bagCost);
            currentBagInfo = new Number[]{totalNumBags, totalBagCost};
        }
        this.order.put(newBag, currentBagInfo);

        for (BaggingAreaController baggingController : this.validBaggingControllers) {
            baggingController.updateExpectedBaggingArea(newBag, weight);
        }

        baggingItemLock = true;

        System.out.println("Reusable bag has been added, you may continue.");

    }

    /**
     * Method to add items to the order TODO: Make this general to handle objects
     * priced by weight instead of just by unit
     */
    public void addItem(ItemAdderController adder, Product newItem, double weight) {
        if ((!this.validItemAdderControllers.contains(adder)) || newItem == null) {
            return;
        }
        if (weight <= 0) {
            return;
        }
        if (baggingItemLock || systemProtectionLock) {
            return;
        }

        Number[] currentItemInfo = new Number[]{BigDecimal.ZERO, BigDecimal.ZERO};

        // Add item to order
        if (this.order.containsKey(newItem)) {
            currentItemInfo = this.order.get(newItem);
        }

        // Add the cost of the new item to the current cost.
        this.cost = this.cost.add(newItem.getPrice());

        currentItemInfo[0] = (currentItemInfo[0].intValue()) + 1;
        currentItemInfo[1] = ((BigDecimal) currentItemInfo[1]).add(newItem.getPrice());

        this.order.put(newItem, currentItemInfo);

        for (BaggingAreaController baggingController : this.validBaggingControllers) {
            baggingController.updateExpectedBaggingArea(newItem, weight);
        }

        baggingItemLock = true;
    }

    public void addItemByPLU(ItemAdderController adder, PriceLookUpCode plucode, String quantity) {
        PLUCodedProduct pluProduct = PLU_PRODUCT_DATABASE.get(plucode);
        //each PluProduct is per kilogram, quatity is the number of kilograms
        if (pluProduct != null) {
            BigDecimal itemQuantity = new BigDecimal(quantity);
            BigDecimal itemPrice = pluProduct.getPrice();
            BigDecimal itemTotalPrice = itemPrice.multiply(itemQuantity);
            double itemWeight = 1 * Double.parseDouble(quantity);//quantity(number of kilograms) * 1 kilogram
            PLUCodedProduct UpdatedProduct = new PLUCodedProduct(pluProduct.getPLUCode(), pluProduct.getDescription(), itemTotalPrice);
            //Needed because addItem calls .getPrice() and the updated price is required
            // Here you can add any additional logic related to the calculated total price

            addItem(adder, UpdatedProduct, itemWeight);
        } else {
            throw new NoSuchElementException("No item could be found with the specified PLU code.");
        }
    }

    //redesigned to make it so that the user can pass in their own database that will be searched
    public void addItemByTextSearch(ItemAdderController adder, String text) {
        String[] keywords = text.split(" ");
        boolean found = false;


        for (BarcodedProduct barprod : BARCODED_PRODUCT_DATABASE.values()) {
            String desc = barprod.getDescription();
            System.out.println(desc);
            for (String word : desc.split(" ")) {
                int matches = 0;

                for (String keyword : keywords) {
                    if (word.equals(keyword)) {
                        matches++;
                    }
                }
                // If all keywords are present in this product's description, then it is matched
                if (matches == keywords.length) {
                    found = true;
                    this.addItem(adder, barprod, barprod.getExpectedWeight());
                    break;
                }
            }

        }
        if (!found) {
            for (PLUCodedProduct pluprod : PLU_PRODUCT_DATABASE.values()) {
                String desc = pluprod.getDescription();
                System.out.println(desc);

                for (String word : desc.split(" ")) {
                    int matches = 0;

                    for (String keyword : keywords) {
                        if (word.equals(keyword)) {
                            matches++;
                        }
                    }
                    // If all keywords are present in this product's description, then it is matched
                    if (matches == keywords.length) {
                        found = true;
                        this.addItemByPLU(adder, pluprod.getPLUCode(), "1");//WIll call when Aman implements add by plu code.
                        break;
                    }
                }
            }
        }
        if (!found) {
            throw new NoSuchElementException("No item could be found in the database with all specified keywords.");
        }
    }
    /*
     * Methods used by BaggingAreaControllers
     */

    public void addToAmountPaid(BigDecimal val) {
        amountPaid = amountPaid.add(val);
    }

    public BigDecimal getRemainingAmount() {
        return getCost().subtract(amountPaid);
    }

    /**
     * Method called by bagging area controllers which says to remove the lock on
     * the station if all controllers for that area agree the items in it are valid.
     */
    public void baggedItemsValid(BaggingAreaController controller) {
        if (!(this.validBaggingControllers.contains(controller))) {
            return;
        }
        // looping over all bagging area controllers and checking if all of them say the
        // contents are valid
        // then we unlock the station.
        boolean unlockStation = true;
        for (BaggingAreaController baggingController : validBaggingControllers) {
            if (!baggingController.getBaggingValid()) {
                unlockStation = false;
                break;
            }
        }
        baggingItemLock = unlockStation;
    }

    void baggedItemsInvalid(BaggingAreaController controller, String ErrorMessage) {
        if (!(this.validBaggingControllers.contains(controller))) {
            return;
        }
        // inform the I/O for both customer and attendant from the error message, this
        // is a placeholder currently.
        System.out.println(ErrorMessage);
        // TODO: Lock system out of processing payments if error in bagging area occurs
    }

    void baggingAreaError(BaggingAreaController controller, String ErrorMessage) {

        if (!(this.validBaggingControllers.contains(controller))) {
            return;
        }
        // inform the I/O for both customer and attendant from the error message, this
        // is specifically
        // for cases where further action might damage the station (eg: the weight on an
        // electronic scale
        // which is used to validate the order is at its limit and further item addition
        // might cause damage).
        // also is a placeholder
        System.out.println(ErrorMessage);
        this.systemProtectionLock = true;
    }

    // If the potential error which could have damaged the system is no longer a
    // threat
    // (eg: if the weight was reduced to below the threshold so its no longer at
    // risk of damaging the system)
    // then the error will be cleared.
    void baggingAreaErrorEnded(BaggingAreaController controller, String OutOfErrorMessage) {
        if (!(this.validBaggingControllers.contains(controller))) {
            return;
        }
        this.systemProtectionLock = false;
    }

    /**
     * Methods used to control the ReceiptPrinterController
     */
    public void printReceipt() {
        // print receipt
        if (this.receiptPrinter != null) {
            // call print receipt method in the ReceiptPrinterController class with the
            // order details and cost
            this.receiptPrinter.printReceipt(this.order, this.cost);
        }
        clearOrder();
    }

    void printerOutOfResources(ReceiptPrinterController controller) {
        if (controller != this.receiptPrinter) {
            return;
        }
        this.needPrinterRefill = true;
    }

    void printerRefilled(ReceiptPrinterController controller) {
        if (controller != this.receiptPrinter) {
            return;
        }
        this.needPrinterRefill = false;
    }

    /**
     * Methods to control the PaymentController
     */

    void completePayment() {
        if (this.baggingItemLock || this.systemProtectionLock) {
            return;
        }
        if (this.cost.compareTo(this.amountPaid) > 0) {
            System.out.println("You haven't paid enough money yet.");
            return;
        }
        if (this.order.keySet().size() == 0) {
            System.out.println("Your order is empty.");
            return;
        }
        if (this.cost.compareTo(this.amountPaid) < 0) {
            this.payingChangeLock = true;
            // This code is inefficient and could be better, too bad!
            ChangeSlotController[] temp = this.changeSlotControllers.toArray(new ChangeSlotController[2]);
            dispenseChange(temp[0]);
        } else {
            printReceipt();
        }
    }

    void dispenseChange(ChangeSlotController controller) {
        if (!changeSlotControllers.contains(controller)) {
            return;
        }
        if ((getRemainingAmount().compareTo(BigDecimal.ZERO) == 0) && payingChangeLock) {
            this.receiptPrinter.printReceipt(this.order, this.cost);
        } else {
            BigDecimal denom = changeDispenserControllers.lastKey();
            while (denom.compareTo(BigDecimal.ZERO) > 0) {
                if ((getRemainingAmount().negate()).compareTo(denom) >= 0) {
                    amountPaid = amountPaid.subtract((denom));
                    changeDispenserControllers.get(denom).emitChange();
                    break;
                } else {
                    if (changeDispenserControllers.lowerKey(denom) != null) {
                        denom = changeDispenserControllers.lowerKey(denom);
                    } else {
                        denom = BigDecimal.ZERO;
                    }
                }
            }
        }
    }

    public void changeDispenseFailed(ChangeDispenserController controller, BigDecimal denom) {
        if (!this.changeDispenserControllers.containsValue(controller)) {
            return;
        }

        if (controller instanceof BillDispenserController) {
            System.out.printf("Bill dispenser with denomination %s out of bills.%n", denom.toString());
        } else {
            System.out.printf("Coin dispenser with denomination %s out of coins.%n", denom.toString());
        }
        this.amountPaid = this.amountPaid.add(denom);
    }

    // since both methods of paying by credit and debit cards are simulated the same
    // way
    // only one method is needed. - Arie
    public void payByCard(CardIssuer source, BigDecimal amount, Card card) {
        if (baggingItemLock || systemProtectionLock || payingChangeLock || source == null) {
            return;
        }
        if (amount.compareTo(getRemainingAmount()) > 0) {
            return;
            // only reason to pay more than the order with card is to mess with the amount
            // of change the system has for some reason
            // so preventing stuff like this would be a good idea.
        }
        for (PaymentController controller : validPaymentControllers) {
            if (controller instanceof CardReaderController) {
                ((CardReaderController) controller).card = card;
                ((CardReaderController) controller).enablePayment(source, card, amount);
            }
        }
        // Needs to return to GUI if fail.
    }

    public void payByGiftCard(BigDecimal amount, GiftCard card) {
        if (baggingItemLock || systemProtectionLock || payingChangeLock) {
            return;
        }
        if (amount.compareTo(getRemainingAmount()) > 0) {
            return;
            // only reason to pay more than the order with card is to mess with the amount
            // of change the system has for some reason
            // so preventing stuff like this would be a good idea.
        }
        for (PaymentController controller : validPaymentControllers) {
            if (controller instanceof CardReaderController) {
                ((CardReaderController) controller).giftCard = card;
                ((CardReaderController) controller).enableGiftPayment(card, amount);
            }
        }
        // Needs to return to GUI if fail.
    }

    /*
     * This method is called when the user indicates they want to add their own bags
     */
    public void addOwnBags() {
        // store the current weight of items in the bagging controller
        for (BaggingAreaController baggingController : validBaggingControllers) {
            ElectronicScaleController scale = (ElectronicScaleController) baggingController;
            double current = scale.getCurrentWeight();
            weight.put(baggingController, current);
            // let the scale know the customer is adding bags to prevent a weight
            // discrepancy
            scale.setAddingBags(true);
        }

        // let the customer know they can add their bags now
        System.out.print("Add bags now\n");

        // at this point, the customer IO must have signalled they are done adding bags
        // to proceed
        // GUI will implement this part to continue to next lines of code

        // store the new weight in bagging area with bags added
        for (BaggingAreaController baggingController : validBaggingControllers) {
            ElectronicScaleController scale = (ElectronicScaleController) baggingController;
            // let the scale know the customer is done adding bags
            scale.setAddingBags(false);
            double current = scale.getCurrentWeight();
            weightWithBags.put(baggingController, current);
        }
        // at this point the system signals to the attendant IO and locks
        systemProtectionLock = true;
        // if the attendant approves adding bags, the system is unblocked
        if (AttendantApproved) {
            // get the weight of the bags and update the expected weight of the bagging area
            // to account for them
            for (BaggingAreaController baggingController : validBaggingControllers) {
                double bagWeight = weightWithBags.get(baggingController) - weight.get(baggingController);
                ElectronicScaleController scale = (ElectronicScaleController) baggingController;
                scale.updateWithBagWeight(bagWeight);
            }
            systemProtectionLock = false;
        } else {
            return;
        }
        // if the attendant has not approved, the request is cancelled
        // thus the current weight of the scale returns to what it was before

        // placeholder for system to tell customer to continue
        System.out.print("You may now continue\n");
    }

    public Map<BaggingAreaController, Double> getWeight() {
        return this.weight;
    }

    public Map<BaggingAreaController, Double> getWeightWithBags() {
        return this.weightWithBags;
    }

    public HashSet<BaggingAreaController> getValidBaggingControllers() {
        return this.validBaggingControllers;
    }


    // Function to Log in
    public boolean Log_in_Attendant(String userID, String password) {
        // Already Logged in
        if (Log_in_Status) {
            throw new SimulationException("An Attendant is currently Logged in");
        }
        // New Log In
        if (Attendant.AttendantList.containsKey(userID) && Attendant.AttendantList.get(userID).equals(password)) {
            // Sets log in status to true
            Log_in_Status = true;
            // Updates the name of current Attendant on the System
            Attendant_ID = userID;

            // Add code for Attendant is permitted to use the station
            System.out.println("The attendant is allowed to use the station");

        } else {
            throw new SimulationException("The login credentials do not match any Attendant.");
        }
        // Return the Log in Status as true if successfully logged in
        return Log_in_Status;

    }

    public boolean Log_Out_Attendant() {
        if (!Log_in_Status) {
            throw new SimulationException("There is no attendant who is currenlty logged in.");

        }
        if (Log_in_Status) {
            // Resets the Attendant ID and Log in Status
            Attendant_ID = null;
            this.Log_in_Status = false;
            // Add code for Attendant is not-permitted to use the station

            System.out.println("The attendant is not allowed to use the station.");
        }
        //Returns the login status as false if succsfully logged out
        return Log_in_Status;

    }


    public void stationStartup(SelfCheckoutStation station) {
        station.baggingArea.enable();
        station.billInput.enable();
        station.billOutput.enable();
        station.billStorage.enable();
        station.billValidator.enable();
        station.cardReader.enable();
        station.coinStorage.enable();
        station.coinTray.enable();
        station.coinValidator.enable();
        station.handheldScanner.enable();
        station.mainScanner.enable();
        station.printer.enable();
        station.scale.enable();
        station.screen.enable();
        for (CoinDispenser coinDispenser : station.coinDispensers.values()) {
            coinDispenser.enable();
        }
        for (BillDispenser billDispenser : station.billDispensers.values()) {
            billDispenser.enable();
        }
    }

    public void stationShutdown(SelfCheckoutStation station) {
        station.baggingArea.disable();
        station.billInput.disable();
        station.billOutput.disable();
        station.billStorage.disable();
        station.billValidator.disable();
        station.cardReader.disable();
        station.coinStorage.disable();
        station.coinTray.disable();
        station.coinValidator.disable();
        station.handheldScanner.disable();
        station.mainScanner.disable();
        station.printer.disable();
        station.scale.disable();
        station.screen.disable();
        for (CoinDispenser coinDispenser : station.coinDispensers.values()) {
            coinDispenser.disable();
        }
        for (BillDispenser billDispenser : station.billDispensers.values()) {
            billDispenser.disable();
        }
    }


    public void insertWithBadPinChecking(CardReader cr, Card card, String pin) throws InvalidPINException {
        Card.CardData carddata = null;
        CardReaderController cc = null;
        try {
            for (PaymentController pc : validPaymentControllers) {
                cc = (CardReaderController) pc;
                cr.tap(card);
                carddata = cc.cardData;
            }
        } catch (IOException e) {
        }
        try {
            cr.insert(card, pin);
        } catch (InvalidPINException E) {
            System.out.println("Bad pin detected");
            if (payCardAttempts.containsKey(carddata.getNumber())) {
                payCardAttempts.put(carddata.getNumber(), payCardAttempts.get(carddata.getNumber()) + 1);
            } else {
                payCardAttempts.put(carddata.getNumber(), 1);
            }
            System.out.println(payCardAttempts.get(carddata.getNumber()));
            if (payCardAttempts.get(carddata.getNumber()) > 3) {
                System.out.println("Signal to bank");
                cc.bank.block(carddata.getNumber());
            }
        } catch (BlockedCardException e) {
            System.out.println("Card is blocked");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMembershipNum() {
        return membershipNum;

    }

}
