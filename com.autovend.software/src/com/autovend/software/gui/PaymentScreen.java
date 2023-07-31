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
package com.autovend.software.gui;

//Necessary imports

import com.autovend.devices.SelfCheckoutStation;
import com.autovend.software.controllers.CheckoutController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class PaymentScreen {

    //PaymentScreen handles the GUI for the payment screen of the SelfCheckoutStation.

    //all the components that are added to the screen
    private final JFrame touchScreenFrame;
    private final JPanel paymentPanel;
    private final JLabel label;
    private final JButton cash;
    private final JButton credit;
    private final JButton debit;
    private final JButton giftCard;
    private final String amountDue;
    private final DefaultTableModel paymentTable;
    private JPanel cashPanel;
    private JPanel creditPanel;
    private JPanel debitPanel;
    private JPanel giftPanel;
    private JPanel paymentSuccessful;
    private JLabel totalDue;
    private JLabel totalDueVal;
    private JLabel msg;
    private JLabel thanksMsg;
    private JLabel paymentMsg;
    private JButton pay;
    private JButton pay1;
    private JButton pay2;
    private JButton pay3;
    private JButton printReceipt;

//	public PaymentScreen(SelfCheckoutStation cStation) {
//
//	}

    public PaymentScreen(SelfCheckoutStation cStation, DefaultTableModel paymentTable, double cartValue) {
        this.paymentTable = paymentTable;
//        Object[] columnData = new Object[paymentTable.getColumnCount()];
//        Object[] rowData = new Object[paymentTable.getRowCount()];
//        System.out.println(Arrays.toString(columnData));
//        System.out.println(Arrays.toString(rowData));
//
//		 for (int i = 0; i < paymentTable.getRowCount(); i++) {
//
//		 }

        // Round the cart value to a proper dollar amount
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        amountDue = df.format(cartValue);
        CheckoutController c = new CheckoutController(cStation);
        //creating a new JFrame
        this.touchScreenFrame = cStation.screen.getFrame();
        this.touchScreenFrame.setExtendedState(JFrame.NORMAL);
        this.touchScreenFrame.setSize(1000, 900);
        this.touchScreenFrame.setResizable(true);


        paymentPanel = new JPanel();
        paymentPanel.setSize(new Dimension(985, 785));
        paymentPanel.setLayout(null);

        label = new JLabel("Please select a payment option:");
        label.setFont(new Font("Times New Roman", Font.BOLD, 18));
        label.setBounds(368, 200, 349, 54);
        paymentPanel.add(label);

        cash = new JButton("Cash");
        cash.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        cash.setBounds(405, 266, 171, 38);
        cash.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //implement the button press
                System.out.println("Paying using cash");
                paymentPanel.setVisible(false);

                cashPanel = new JPanel();
                cashPanel.setSize(new Dimension(985, 785));
                cashPanel.setLayout(null);

                totalDue = new JLabel("Total Due :");
                totalDue.setFont(new Font("Times New Roman", Font.BOLD, 25));
                totalDue.setBounds(358, 200, 359, 54);
                //get the amount due from the software.
                //for testing, i have used d(a BigDecimal) for now. when the software is plugged, d should be replaced with the amount that is due

                String due = amountDue;

                totalDueVal = new JLabel(due);
                totalDueVal.setFont(new Font("Times New Roman", Font.BOLD, 22));
                totalDueVal.setBounds(533, 200, 350, 54);
                cashPanel.add(totalDue);
                cashPanel.add(totalDueVal);

                msg = new JLabel("Please insert cash into the machine and click pay");
                msg.setFont(new Font("Times New Roman", Font.BOLD, 22));
                msg.setBounds(356, 250, 550, 54);
                cashPanel.add(msg);

                pay = new JButton("PAY");
                pay.setFont(new Font("Times New Roman", Font.PLAIN, 22));
                pay.setBounds(405, 350, 171, 45);


                pay.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        cashPanel.setVisible(false);


                        //-----------------------------------------------------------------------------------------------------------
                        //when the pay button is pressed, the pay method from the software should be called
                        //and the amount due should be updated.
                        //when the amount due becomes zero, it takes you to the next screen(print receipt)
                        //for testing, i have true for now. true should be replaced with BigDecimal.ZERO ig.

                        //-----------------------------------------------------------------------------------------------------------
                        cashPanel.setVisible(false);
                        paymentSuccessful = new JPanel();
                        paymentSuccessful.setSize(new Dimension(985, 785));
                        paymentSuccessful.setLayout(null);

                        paymentMsg = new JLabel("Payment Successful");
                        paymentMsg.setFont(new Font("Times New Roman", Font.BOLD, 25));
                        paymentMsg.setBounds(380, 213, 359, 54);
                        paymentSuccessful.add(paymentMsg);

                        thanksMsg = new JLabel("Thank you for shopping with us today");
                        thanksMsg.setFont(new Font("Times New Roman", Font.BOLD, 22));
                        thanksMsg.setBounds(320, 273, 400, 54);
                        paymentSuccessful.add(thanksMsg);

                        printReceipt = new JButton("Print Receipt");
                        printReceipt.setFont(new Font("Times New Roman", Font.PLAIN, 22));
                        printReceipt.setBounds(390, 350, 200, 45);

                        printReceipt.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                //call the printReceipt method here
                                c.printReceipt();
                            }
                        });

                        paymentSuccessful.add(printReceipt);
                        touchScreenFrame.add(paymentSuccessful);
                    }
                });
                cashPanel.add(pay);
                touchScreenFrame.add(cashPanel);
            }
        });
        paymentPanel.add(cash);


        credit = new JButton("Credit");
        credit.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        credit.setBounds(405, 324, 171, 38);
        credit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //implement the button press
                System.out.println("Paying using Credit Card");


                paymentPanel.setVisible(false);

                creditPanel = new JPanel();
                creditPanel.setSize(new Dimension(985, 785));
                creditPanel.setLayout(null);

                totalDue = new JLabel("Total Due :");
                totalDue.setFont(new Font("Times New Roman", Font.BOLD, 25));
                totalDue.setBounds(358, 200, 359, 54);
                //get the amount due from the software.
                //for testing, i have used d(a BigDecimal) for now. when the software is plugged, d should be replaced with the amount that is due
                String due = amountDue;
                totalDueVal = new JLabel(due);
                totalDueVal.setFont(new Font("Times New Roman", Font.BOLD, 22));
                totalDueVal.setBounds(533, 200, 350, 54);
                creditPanel.add(totalDue);
                creditPanel.add(totalDueVal);

                msg = new JLabel("Please insert/tap/swipe your card and click pay");
                msg.setFont(new Font("Times New Roman", Font.BOLD, 22));
                msg.setBounds(356, 250, 550, 54);
                creditPanel.add(msg);

                pay1 = new JButton("PAY");
                pay1.setFont(new Font("Times New Roman", Font.PLAIN, 22));
                pay1.setBounds(405, 350, 171, 45);


                pay1.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        creditPanel.setVisible(false);

                        //when the pay button is pressed, the pay method from the software should be called
                        //and the amount due should be updated.
                        //when the amount due becomes zero, it takes you to the next screen(print receipt)
                        //for testing, i have true for now. true should be replaced with BigDecimal.ZERO ig.

                        if (true) {
                            creditPanel.setVisible(false);
                            paymentSuccessful = new JPanel();
                            paymentSuccessful.setSize(new Dimension(985, 785));
                            paymentSuccessful.setLayout(null);

                            paymentMsg = new JLabel("Payment Successful");
                            paymentMsg.setFont(new Font("Times New Roman", Font.BOLD, 25));
                            paymentMsg.setBounds(380, 213, 359, 54);
                            paymentSuccessful.add(paymentMsg);

                            thanksMsg = new JLabel("Thank you for shopping with us today");
                            thanksMsg.setFont(new Font("Times New Roman", Font.BOLD, 22));
                            thanksMsg.setBounds(320, 273, 400, 54);
                            paymentSuccessful.add(thanksMsg);

                            printReceipt = new JButton("Print Receipt");
                            printReceipt.setFont(new Font("Times New Roman", Font.PLAIN, 22));
                            printReceipt.setBounds(390, 350, 200, 45);

                            printReceipt.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    //call the printReceipt method here
                                    c.printReceipt();
                                }
                            });

                            paymentSuccessful.add(printReceipt);
                            touchScreenFrame.add(paymentSuccessful);


                        } else {

                            //else update the amount due(d) after partial payment and go back to the main screen
                            paymentPanel.setVisible(true);
                        }

                    }
                });
                creditPanel.add(pay1);

                touchScreenFrame.add(creditPanel);

            }

        });
        paymentPanel.add(credit);


        debit = new JButton("Debit");
        debit.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        debit.setBounds(405, 384, 171, 38);
        debit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //implement the button press
                System.out.println("Paying using Debit Card");

                paymentPanel.setVisible(false);

                debitPanel = new JPanel();
                debitPanel.setSize(new Dimension(985, 785));
                debitPanel.setLayout(null);

                totalDue = new JLabel("Total Due :");
                totalDue.setFont(new Font("Times New Roman", Font.BOLD, 25));
                totalDue.setBounds(358, 200, 359, 54);
                //get the amount due from the software.
                //for testing, i have used d(a BigDecimal) for now. when the software is plugged, d should be replaced with the amount that is due
                String due = amountDue;
                totalDueVal = new JLabel(due);
                totalDueVal.setFont(new Font("Times New Roman", Font.BOLD, 22));
                totalDueVal.setBounds(533, 200, 350, 54);
                debitPanel.add(totalDue);
                debitPanel.add(totalDueVal);

                msg = new JLabel("Please insert/tap/swipe your card and click pay");
                msg.setFont(new Font("Times New Roman", Font.BOLD, 22));
                msg.setBounds(356, 250, 550, 54);
                debitPanel.add(msg);

                pay2 = new JButton("PAY");
                pay2.setFont(new Font("Times New Roman", Font.PLAIN, 22));
                pay2.setBounds(405, 350, 171, 45);

                pay2.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        debitPanel.setVisible(false);

                        //when the pay button is pressed, the pay method from the software should be called
                        //and the amount due should be updated.
                        //when the amount due becomes zero, it takes you to the next screen(print receipt)
                        //for testing, i have it as true for now

                        if (true) {
                            debitPanel.setVisible(false);
                            paymentSuccessful = new JPanel();
                            paymentSuccessful.setSize(new Dimension(985, 785));
                            paymentSuccessful.setLayout(null);

                            paymentMsg = new JLabel("Payment Successful");
                            paymentMsg.setFont(new Font("Times New Roman", Font.BOLD, 25));
                            paymentMsg.setBounds(380, 213, 359, 54);
                            paymentSuccessful.add(paymentMsg);

                            thanksMsg = new JLabel("Thank you for shopping with us today");
                            thanksMsg.setFont(new Font("Times New Roman", Font.BOLD, 22));
                            thanksMsg.setBounds(320, 273, 400, 54);
                            paymentSuccessful.add(thanksMsg);

                            printReceipt = new JButton("Print Receipt");
                            printReceipt.setFont(new Font("Times New Roman", Font.PLAIN, 22));
                            printReceipt.setBounds(390, 350, 200, 45);

                            printReceipt.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    //call the printReceipt method here
                                    c.printReceipt();


                                }
                            });

                            paymentSuccessful.add(printReceipt);
                            touchScreenFrame.add(paymentSuccessful);


                        } else {

                            //else update the amount due(d) after partial payment and go back to the main screen
                            paymentPanel.setVisible(true);
                        }

                    }
                });
                debitPanel.add(pay2);

                touchScreenFrame.add(debitPanel);

            }

        });
        paymentPanel.add(debit);


        giftCard = new JButton("Gift Card");
        giftCard.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        giftCard.setBounds(405, 451, 171, 38);
        giftCard.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //implement the button press
                System.out.println("Paying using Gift Card");

                paymentPanel.setVisible(false);

                giftPanel = new JPanel();
                giftPanel.setSize(new Dimension(985, 785));
                giftPanel.setLayout(null);

                totalDue = new JLabel("Total Due :");
                totalDue.setFont(new Font("Times New Roman", Font.BOLD, 25));
                totalDue.setBounds(358, 200, 359, 54);
                //get the amount due from the software.
                //for testing, i have used d(a BigDecimal) for now. when the software is plugged, d should be replaced with the amount that is due
                String due = amountDue;
                totalDueVal = new JLabel(due);
                totalDueVal.setFont(new Font("Times New Roman", Font.BOLD, 22));
                totalDueVal.setBounds(533, 200, 350, 54);
                giftPanel.add(totalDue);
                giftPanel.add(totalDueVal);

                msg = new JLabel("Please insert/tap/swipe your card and click pay");
                msg.setFont(new Font("Times New Roman", Font.BOLD, 22));
                msg.setBounds(356, 250, 550, 54);
                giftPanel.add(msg);

                pay3 = new JButton("PAY");
                pay3.setFont(new Font("Times New Roman", Font.PLAIN, 22));
                pay3.setBounds(405, 350, 171, 45);

                pay3.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        giftPanel.setVisible(false);

                        //when the pay button is pressed, the pay method from the software should be called
                        //and the amount due should be updated.
                        //when the amount due becomes zero, it takes you to the next screen(print receipt)
                        //for testing, i have false for now. true should be replaced with BigDecimal.ZERO ig.

                        if (true) {
                            giftPanel.setVisible(false);
                            paymentSuccessful = new JPanel();
                            paymentSuccessful.setSize(new Dimension(985, 785));
                            paymentSuccessful.setLayout(null);

                            paymentMsg = new JLabel("Payment Successful");
                            paymentMsg.setFont(new Font("Times New Roman", Font.BOLD, 25));
                            paymentMsg.setBounds(380, 213, 359, 54);
                            paymentSuccessful.add(paymentMsg);

                            thanksMsg = new JLabel("Thank you for shopping with us today");
                            thanksMsg.setFont(new Font("Times New Roman", Font.BOLD, 22));
                            thanksMsg.setBounds(320, 273, 400, 54);
                            paymentSuccessful.add(thanksMsg);

                            printReceipt = new JButton("Print Receipt");
                            printReceipt.setFont(new Font("Times New Roman", Font.PLAIN, 22));
                            printReceipt.setBounds(390, 350, 200, 45);

                            printReceipt.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    //call the printReceipt method here
                                    c.printReceipt();


                                }
                            });

                            paymentSuccessful.add(printReceipt);
                            touchScreenFrame.add(paymentSuccessful);


                        } else {

                            //else update the amount due(d) after partial payment and go back to the main screen
                            paymentPanel.setVisible(true);
                        }

                    }
                });

                giftPanel.add(pay3);
                touchScreenFrame.add(giftPanel);

            }

        });
        paymentPanel.add(giftCard);


        this.touchScreenFrame.getContentPane().add(paymentPanel);
        this.touchScreenFrame.setVisible(true);


    }
}