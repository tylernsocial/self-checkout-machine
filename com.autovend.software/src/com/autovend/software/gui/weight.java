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

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class weight {

    // PaymentScreen handles the GUI for the payment screen of the SelfCheckoutStation.

    // all the components that are added to the screen
    private final JFrame touchScreenFrame1;
    private final JPanel weightchange;
    private final JLabel label;

    public weight() {

        touchScreenFrame1 = new JFrame("Weight Discrepancy!");
        touchScreenFrame1.setSize(new Dimension(1000, 900));
        touchScreenFrame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        touchScreenFrame1.getContentPane().setBackground(new Color(247, 247, 247));

        weightchange = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 150));
        weightchange.setBackground(new Color(247, 247, 247));
        Border paddingBorder = BorderFactory.createEmptyBorder(20, 50, 20, 50);
        Border border = BorderFactory.createLineBorder(new Color(0, 0, 0), 2);
        weightchange.setBorder(BorderFactory.createCompoundBorder(border, paddingBorder));

        label = new JLabel("There is weight discrepancy! Wait for attendant approval..", JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 26));
        label.setForeground(new Color(0, 102, 204));
        weightchange.add(label, BorderLayout.CENTER);

        touchScreenFrame1.getContentPane().add(weightchange);
        touchScreenFrame1.setVisible(true);
        touchScreenFrame1.setResizable(false);
    }

    public static void main(String[] args) {
        new weight();
    }
}
