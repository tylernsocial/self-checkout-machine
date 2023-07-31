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
import com.autovend.devices.SupervisionStation;
import com.autovend.software.controllers.AttendantController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Currency;


public class AttendantLogin {
    //AttendantLogin class handles the login screen for the attendant station.

    //all the components that are added to the screen
    private final JFrame touchScreenFrame;
    private final JLayeredPane logInPane;
    private final JPanel logInScreen;
    private final JTextField userName;
    private final JPasswordField password;
    private final JLabel userLabel;
    private final JLabel passwordLabel;
    private final JButton loginButton;
    private final JLabel failMessage;
    private final JLabel loginText;

    private AttendantController attendant;


    //constructor
    public AttendantLogin(SupervisionStation aStation) {


        //creating a new JFrame
        this.touchScreenFrame = aStation.screen.getFrame();
        this.touchScreenFrame.setExtendedState(JFrame.NORMAL);
        this.touchScreenFrame.setSize(1000, 900);
        this.touchScreenFrame.setResizable(true);
        //creating a new layered pane
        logInPane = new JLayeredPane();
        //setting the size of the pane
        logInPane.setSize(new Dimension(1000, 900));
        //creating a new JPanel for the main log in screen
        logInScreen = new JPanel();
        //setting the size of the panel
        logInScreen.setSize(new Dimension(985, 785));
        //using absolute layout
        logInScreen.setLayout(null);

        //creating a new JLabel
        loginText = new JLabel("Attendant Login");
        loginText.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 22));
        loginText.setBounds(376, 99, 201, 51);
        //adding the label to the logInScreen panel
        logInScreen.add(loginText);
        //creating a new text field
        userName = new JTextField();
        userName.setBounds(534, 235, 201, 51);
        userName.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        userName.setHorizontalAlignment(SwingConstants.CENTER);

        userName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                System.out.println("clicked username field");
            }
        });

        //adding the text field to the logInScreen panel
        logInScreen.add(userName);
        userName.setColumns(10);

        //creating a new password field
        password = new JPasswordField();
        password.setBounds(534, 337, 201, 51);
        password.setHorizontalAlignment(SwingConstants.CENTER);
        password.setFont(new Font("Times New Roman", Font.PLAIN, 16));

        password.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                System.out.println("clicked password field");

            }
        });

        //adding the password field to the logInScreen panel
        logInScreen.add(password);

        //creating a new label
        userLabel = new JLabel("Enter Your Username:");
        userLabel.setBounds(135, 236, 189, 46);
        userLabel.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        //adding the label to the logInScreen panel
        logInScreen.add(userLabel);

        //creating a new label
        passwordLabel = new JLabel("Enter Your Password:");
        passwordLabel.setBounds(135, 338, 189, 46);
        passwordLabel.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        //adding the label to the logInScreen panel
        logInScreen.add(passwordLabel);

        //creating a new button
        loginButton = new JButton("Login");
        loginButton.setBounds(534, 446, 201, 51);
        loginButton.setFont(new Font("Times New Roman", Font.BOLD, 18));
        //adding an action listener to the button
        loginButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //check for login success or failure

                attendant = new AttendantController(userName.getText(), password.getText());

                if (attendant.AttendantList.containsValue(password.getText()) && attendant.AttendantList.containsKey(userName.getText())) {
                    //if the credentials match,go to the next panel(attendant main screen)do not match, display the try again message

                    logInScreen.setVisible(false);

                    AttendantMain attendantGui = new AttendantMain(aStation);


                } else {

                    //display the try again message
                    logInScreen.setVisible(true);
                    failMessage.setVisible(true);
                }

            }

        });
        //adding the button the logInScreen panel
        logInScreen.add(loginButton);


        //creating a new label for incorrect credentials
        failMessage = new JLabel("Incorrect credentials, please try again.");
        failMessage.setFont(new Font("Times New Roman", Font.BOLD, 18));
        failMessage.setForeground(Color.RED);
        failMessage.setBounds(307, 172, 331, 38);
        //adding the label to the logInScreen panel
        logInScreen.add(failMessage);
        failMessage.setVisible(false);

        //setting the frame to be visible
        touchScreenFrame.setVisible(true);
        //adding the layered pane to the frame
        touchScreenFrame.getContentPane().add(logInPane);
        //adding the panels to the layered pane
        logInPane.add(logInScreen, Integer.valueOf(1));
        //setting the layered pane to be visible
        logInPane.setVisible(true);


    }


    public static void main(String[] args) {
        SupervisionStation aStation = new SupervisionStation();
        aStation.add(new SelfCheckoutStation(Currency.getInstance("CAD"), new int[]{1, 24}, new BigDecimal[]{BigDecimal.ONE}, 1, 1));
        aStation.add(new SelfCheckoutStation(Currency.getInstance("CAD"), new int[]{1, 24}, new BigDecimal[]{BigDecimal.ONE}, 1, 1));
        aStation.add(new SelfCheckoutStation(Currency.getInstance("CAD"), new int[]{1, 24}, new BigDecimal[]{BigDecimal.ONE}, 1, 1));
        aStation.add(new SelfCheckoutStation(Currency.getInstance("CAD"), new int[]{1, 24}, new BigDecimal[]{BigDecimal.ONE}, 1, 1));
        aStation.add(new SelfCheckoutStation(Currency.getInstance("CAD"), new int[]{1, 24}, new BigDecimal[]{BigDecimal.ONE}, 1, 1));
        aStation.add(new SelfCheckoutStation(Currency.getInstance("CAD"), new int[]{1, 24}, new BigDecimal[]{BigDecimal.ONE}, 1, 1));
        aStation.add(new SelfCheckoutStation(Currency.getInstance("CAD"), new int[]{1, 24}, new BigDecimal[]{BigDecimal.ONE}, 1, 1));
        new AttendantLogin(aStation);
    }

}
