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

import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SupervisionStation;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public class AttendantMain {

    JFrame touchScreenFrame;
    JLabel titleText;
    JPanel mainPanel;
    JScrollPane stationScrollPane;
    JButton logoutButton;
    JPanel stationListPane;

    public AttendantMain(SupervisionStation attendantStation) {

        this.touchScreenFrame = attendantStation.screen.getFrame();
        this.touchScreenFrame.setExtendedState(JFrame.NORMAL);
        this.touchScreenFrame.setSize(1000, 900);
        this.touchScreenFrame.setResizable(true);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        this.touchScreenFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        titleText = new JLabel("Attendant Terminal");
        titleText.setFont(new Font("Tahoma", Font.BOLD, 20));
        logoutButton = new JButton("Logout");

        logoutButton.addActionListener(actionEvent -> logoutButtonPressed(attendantStation));

        stationListPane = new JPanel();
        stationListPane.setLayout(new GridLayout(0, 1, 0, 30));
        List<SelfCheckoutStation> supervisedStations = attendantStation.supervisedStations();
        for (int i = 0; i < supervisedStations.size(); i++) {
            SelfCheckoutStation checkoutStation = supervisedStations.get(i);
            StationStatusBar tempBar = new StationStatusBar(checkoutStation, i + 1);
            tempBar.setPreferredSize(new Dimension(980, 100));
            stationListPane.add(tempBar);
        }

        stationScrollPane = new JScrollPane(stationListPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mainPanel.add(titleText);
        mainPanel.add(stationScrollPane);
        mainPanel.add(logoutButton);
        stationScrollPane.revalidate();

        this.touchScreenFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SupervisionStation attendantStation = new SupervisionStation();
        //add a bunch of checkout stations to test scrolling
        attendantStation.add(new SelfCheckoutStation(Currency.getInstance("CAD"), new int[]{1, 24}, new BigDecimal[]{BigDecimal.ONE}, 1, 1));
        attendantStation.add(new SelfCheckoutStation(Currency.getInstance("CAD"), new int[]{1, 24}, new BigDecimal[]{BigDecimal.ONE}, 1, 1));
        attendantStation.add(new SelfCheckoutStation(Currency.getInstance("CAD"), new int[]{1, 24}, new BigDecimal[]{BigDecimal.ONE}, 1, 1));
        attendantStation.add(new SelfCheckoutStation(Currency.getInstance("CAD"), new int[]{1, 24}, new BigDecimal[]{BigDecimal.ONE}, 1, 1));
        attendantStation.add(new SelfCheckoutStation(Currency.getInstance("CAD"), new int[]{1, 24}, new BigDecimal[]{BigDecimal.ONE}, 1, 1));
        attendantStation.add(new SelfCheckoutStation(Currency.getInstance("CAD"), new int[]{1, 24}, new BigDecimal[]{BigDecimal.ONE}, 1, 1));
        attendantStation.add(new SelfCheckoutStation(Currency.getInstance("CAD"), new int[]{1, 24}, new BigDecimal[]{BigDecimal.ONE}, 1, 1));
        AttendantMain attendantGUI = new AttendantMain(attendantStation);
    }

    public void logoutButtonPressed(SupervisionStation attendantStation) {
        //logout action
        mainPanel.setVisible(false);
        AttendantLogin logInScreen = new AttendantLogin(attendantStation);
    }

    static class StationStatusBar extends JPanel {

        SelfCheckoutStation selfCheckoutStation;
        JLabel stationTitle;
        JButton disableButton;
        JLabel warningField;
        JButton weightDiscrepancyButton;
        JButton removeApproveButton;

        public StationStatusBar(SelfCheckoutStation checkoutStationIn, int ID) {
            this.selfCheckoutStation = checkoutStationIn;
            this.setBackground(Color.LIGHT_GRAY);
            this.setSize(980, 100);

            this.setBorder(new LineBorder(Color.BLACK));
            this.setLayout(null);


            stationTitle = new JLabel(String.format(" Station %d", ID));
            disableButton = new JButton("Disable Station");
            warningField = new JLabel("Station running normally"); //might replace with a JList?
            weightDiscrepancyButton = new JButton("Weight discrepancy detected.\n Approve?");
            removeApproveButton = new JButton("Approve requested removal");

            disableButton.addActionListener(actionEvent -> disableButtonPressed());
            weightDiscrepancyButton.addActionListener(actionEvent -> weightDiscrepancyButtonPressed());
            removeApproveButton.addActionListener(actionEvent -> removeButtonPressed());

            stationTitle.setBounds(0, 0, 100, 100);
            disableButton.setBounds(100, 0, 200, 100);
            warningField.setBounds(300, 0, 300, 100);
            weightDiscrepancyButton.setBounds(600, 0, 200, 100);
            removeApproveButton.setBounds(800, 0, 200, 100);

            warningField.setHorizontalTextPosition(SwingConstants.CENTER);
            stationTitle.setFont(new Font("Tahoma", Font.BOLD, 13));

            this.add(stationTitle);
            this.add(disableButton);
            this.add(warningField);
            this.add(weightDiscrepancyButton);
            this.add(removeApproveButton);

            stationTitle.setVisible(true);
            disableButton.setVisible(true);
            warningField.setVisible(true);
            weightDiscrepancyButton.setVisible(false);
            removeApproveButton.setVisible(false);

            this.setVisible(true);
        }

        public void disableButtonPressed() {
            String buttonText = disableButton.getText();
            if (buttonText.equals("Disable Station")) {
                //code to disable station
                disableButton.setText("Enable Station");
            } else {
                //code to enable station
                disableButton.setText("Disable Station");
            }
        }

        //has no trigger atm
        public void weightDiscrepancyEventTriggered() {
            weightDiscrepancyButton.setVisible(true);
        }

        public void weightDiscrepancyButtonPressed() {
            //code to end weight discrepancy event at station
            weightDiscrepancyButton.setVisible(false);
        }

        //has no trigger atm
        public void removalEventTriggered() {
            weightDiscrepancyButton.setVisible(true);
        }

        public void removeButtonPressed() {
            //code to approve removal at station
            weightDiscrepancyButton.setVisible(false);
        }

        //these methods have no triggers atm
        public void lowInkEventTriggered() {
            warningField.setText("Low ink at station");
        }

        public void lowInkEventEnded() {
            warningField.setText("Station running normally");
        }

        public void lowPaperEventTriggered() {
            warningField.setText("Low paper at station");
        }

        public void lowPaperEventEnded() {
            warningField.setText("Station running normally");
        }

    }

}
