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

import com.autovend.Numeral;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.software.controllers.BillPaymentController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.MembershipCardController;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;

public class CustomerGui {

    final static boolean shouldFill = true;
    final static boolean shouldWeightX = true;
    private final CheckoutController checkoutController;
    private final MembershipCardController membershipCardController;
    private final JLayeredPane layeredPane;
    // these are using to call from attendantmain
    JFrame touchScreenFrame;
    boolean oB = false;            // own bag
    JPanel ownBagAdded;
    boolean audioButtonOn = false;
    boolean selectedPLU = false;
    boolean selectedMembership = false;
    // Put data here to access the payment table
    String[][] data = new String[100][3];
    String[] dataNeo = new String[100];
    int dataCount = 0;
    String[] cName = {"Item ", "@cost per unit", "Total cost"};
    DefaultTableModel dataModel = new DefaultTableModel(data, cName);
    DefaultTableModel paymentTableModel = new DefaultTableModel(null, cName);
    double cartValue = 0.00;
    // create a constraints object
    GridBagConstraints c = new GridBagConstraints();
    private JComboBox<String> languageBox;
    private JPanel tapScreenPanel;
    private JPanel mainPanel;
    private JPanel secondaryPanel;
    private JPanel keyboardPanel;
    private JButton PLUTextButton;
    private JButton itemCata;
    private JButton memberTextButton;
    private JButton purchaseBagsButton;
    private JButton removeItemButton;
    private JButton screensaver;
    private JButton helpButton;
    private JButton audioButton;
    private JButton paymentButton;
    private JScrollPane scrollPane;
    private JTable table;
    private JButton keyZero;
    private JButton keyOne;
    private JButton keyTwo;
    private JButton keyThree;
    private JButton keyFour;
    private JButton keyFive;
    private JButton keySix;
    private JButton keySeven;
    private JButton keyEight;
    private JButton keyNine;
    private JButton keyEnter;
    private JButton keyExit;
    private JTextField keyboardTextField;
    private String keyboardText;
    private int bagsCount = 0;
    private double bagsValue = bagsCount * 0.1;
    // use to count digits
    private int tempCount = 0;

    public CustomerGui(SelfCheckoutStation cStation, CheckoutController checkoutController, MembershipCardController membershipCardController, int ID) {

        this.membershipCardController = membershipCardController;
        this.touchScreenFrame = cStation.screen.getFrame();
        this.touchScreenFrame.setExtendedState(JFrame.NORMAL);
        this.touchScreenFrame.setSize(1000, 900);
        this.touchScreenFrame.setLocationRelativeTo(null);
        this.touchScreenFrame.setUndecorated(false);
        this.touchScreenFrame.setTitle(String.format(" Station %d", ID));
        layeredPane = new JLayeredPane();
        this.touchScreenFrame.getContentPane().add(layeredPane, BorderLayout.CENTER);
        this.checkoutController = checkoutController;

        setUpMainPanel(cStation);
        setUpSecondaryPanel();
        setUpNumericKeyboard(0);
        tapScreen();
        ownBag();

        // use this if you only want to run customergui
        //this.touchScreenFrame.setVisible(true);
        // use this if you want to start from attendantgui
        this.touchScreenFrame.setVisible(true);

    }

    public static void main(String[] args) {
        int[] billDenominations = new int[]{5, 10, 20, 50, 100};
        BigDecimal[] coinDenominations = new BigDecimal[]{new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal(100), new BigDecimal(200)};

        SelfCheckoutStation selfCheckoutStation = new SelfCheckoutStation(Currency.getInstance("CAD"), billDenominations, coinDenominations, 200, 1);
        CheckoutController checkoutController = new CheckoutController(selfCheckoutStation);
        BillPaymentController billPaymentControllerStub = new BillPaymentController(selfCheckoutStation.billValidator);
        billPaymentControllerStub.setMainController(checkoutController);
        checkoutController.registerPaymentController(billPaymentControllerStub);
        SelfCheckoutStation cStation = new SelfCheckoutStation(Currency.getInstance("CAD"), billDenominations, coinDenominations, 1, 1);
        MembershipCardController membershipController = new MembershipCardController();
        CustomerGui newGui = new CustomerGui(cStation, checkoutController, membershipController, 1);
    }

    private void setUpMainPanel(SelfCheckoutStation cStation) {
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.setBounds(0, 0, 984, 785);
        layeredPane.add(mainPanel);
        mainPanel.setLayout(null);

        PLUTextButton = new JButton("Scan a barcode or tap here to search with PLU");
        PLUTextButton.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        PLUTextButton.setOpaque(true);
        PLUTextButton.setBackground(Color.WHITE);
        PLUTextButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        PLUTextButton.setBounds(42, 31, 468, 40);
        PLUTextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedPLU = true;
                // 0 means there is no requirement to check
                openKeyboard(0);
            }
        });
        mainPanel.add(PLUTextButton);

        itemCata = new JButton("Tap here to browse our catalogue of favorite items!");
        itemCata.setFont(new Font("Tahoma", Font.PLAIN, 15));
        itemCata.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cataPopup();
            }
        });

        itemCata.setBounds(42, 106, 468, 40);
        mainPanel.add(itemCata);

        memberTextButton = new JButton("Sign in with membership for rewards");
        memberTextButton.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        memberTextButton.setOpaque(true);
        memberTextButton.setBackground(Color.WHITE);
        memberTextButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        memberTextButton.setBounds(42, 183, 468, 40);
        memberTextButton.setEnabled(true);
        memberTextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedMembership = false;
                // 1 means there is a requirement to meet before enter, in this case: 12 digits
                openKeyboard(1);
                // if member logged in, change text and disable, idk why it doesnt work rn
                if (selectedMembership) {
                    memberTextButton.setText("Membership logged in!");
                    memberTextButton.setEnabled(false);
                }
            }
        });
        mainPanel.add(memberTextButton);

        purchaseBagsButton = new JButton("Purchase Bags");
        purchaseBagsButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        purchaseBagsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                purchaseBag();
            }
        });

        purchaseBagsButton.setBounds(42, 256, 468, 40);
        mainPanel.add(purchaseBagsButton);

        /* Adding RigidAreas. RigidAreas simply ensure that no component
         * enters a specific part of the screen. */
        Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
        rigidArea.setBounds(42, 70, 468, 39);
        mainPanel.add(rigidArea);

        Component rigidArea2 = Box.createRigidArea(new Dimension(20, 20));
        rigidArea2.setBounds(42, 144, 468, 39);
        mainPanel.add(rigidArea2);

        Component rigidArea3 = Box.createRigidArea(new Dimension(20, 20));
        rigidArea3.setBounds(42, 219, 468, 39);
        mainPanel.add(rigidArea3);

        // can only remove last item rn
        removeItemButton = new JButton("Remove an Item");
        removeItemButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        removeItemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainPanel.setVisible(false);
//				for (int i = 0; i <= )
//				paymentTableModel.get
                Remove remove = new Remove(cStation, dataNeo, dataCount);
            }
        });
        removeItemButton.setBounds(42, 329, 468, 40);
        mainPanel.add(removeItemButton);
        setUpPaymentTable();
        setUpPayment(cStation);
    }

    private void setUpSecondaryPanel() {
        secondaryPanel = new JPanel();
        secondaryPanel.setBackground(Color.LIGHT_GRAY);
        layeredPane.setLayer(secondaryPanel, 2);
        secondaryPanel.setBounds(0, 784, 984, 77);
        layeredPane.add(secondaryPanel);
        secondaryPanel.setLayout(null);
        setUpLanguage();
        setUpAudio();
        setUpHelp();
    }

    // Purchase bag
    private void purchaseBag() {
        bagsCount = 0;

        JPanel purBagPane = new JPanel();
        layeredPane.setLayer(purBagPane, 1);
        purBagPane.setBounds(0, 0, 1000, 900);
        purBagPane.setBackground(Color.LIGHT_GRAY);
        layeredPane.add(purBagPane);
        purBagPane.setLayout(new GridBagLayout());

        // Set up gridbagconstraints
        GridBagConstraints tempC = new GridBagConstraints();

        // Back to main screen button
        JButton back = new JButton("Back to main screen");
        back.setFont(new Font("Tahoma", Font.PLAIN, 15));
        back.setPreferredSize(new Dimension(200, 50));
        back.setBounds(392, 20, 200, 50);
        // add to panel
        tempC.gridx = 1;
        tempC.gridy = 0;
        tempC.fill = GridBagConstraints.HORIZONTAL;
        tempC.anchor = GridBagConstraints.NORTH;
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bagsCount = 0;
                purBagPane.setVisible(false);
            }
        });
        purBagPane.add(back, c);

        // Control number of bags customer want to purchase

        JTextField tempBagsCount = new JTextField();
        tempBagsCount.setEditable(false);
        tempBagsCount.setFont(new Font("Tahoma", Font.PLAIN, 50));
        tempBagsCount.setHorizontalAlignment(JTextField.CENTER);
        Border tempB = BorderFactory.createLineBorder(Color.black);
        tempBagsCount.setBorder(tempB);
        tempBagsCount.setText(String.valueOf(bagsCount));
        tempBagsCount.setPreferredSize(new Dimension(200, 200));
        tempBagsCount.setBounds(442, 317, 100, 100);
        tempC.gridx = 1;
        tempC.gridy = 1;
        purBagPane.add(tempBagsCount, c);

        JButton minus = new JButton("-");
        minus.setFont(new Font("Tahome", Font.BOLD, 50));
        minus.setPreferredSize(new Dimension(200, 200));
        minus.setBounds(300, 317, 100, 100);
        tempC.gridx = 0;
        tempC.gridy = 1;
        tempC.anchor = GridBagConstraints.CENTER;
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //purBagPane.setVisible(false);
                bagsCount = bagsCount - 1;
                tempBagsCount.setText(String.valueOf(bagsCount));
                if (bagsCount <= 0) {
                    minus.setEnabled(false);
                }
            }
        });
        if (bagsCount <= 0) {
            minus.setEnabled(false);
        }
        purBagPane.add(minus, c);

        JButton purchase = new JButton("Purchase Bag(s)");
        purchase.setFont(new Font("Tahome", Font.PLAIN, 15));
        purchase.setPreferredSize(new Dimension(200, 50));
        purchase.setBounds(392, 500, 200, 50);
        if (bagsCount <= 0) {
            purchase.setEnabled(false);
        }
        tempC.gridx = 1;
        tempC.gridy = 2;
        tempC.anchor = GridBagConstraints.CENTER;
        purchase.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Add bagsCount bags to payment summary

                purBagPane.setVisible(false);
                bagsValue = bagsCount * 0.1;
//				System.out.println(bagsCount);
//				System.out.println(bagsValue);

                DecimalFormat tempValue = new DecimalFormat("0.00");
                data[dataCount] = new String[]{"Bag(s) @$0.10", String.valueOf(bagsCount), "$" + tempValue.format(bagsValue)};
                dataCount++;
                updateTable(new String[]{"Bag(s) @$0.10", String.valueOf(bagsCount), "$" + tempValue.format(bagsValue)});
            }
        });
        purBagPane.add(purchase, c);

        JButton add = new JButton("+");
        add.setFont(new Font("Tahome", Font.BOLD, 50));
        add.setPreferredSize(new Dimension(200, 200));
        add.setBounds(584, 317, 100, 100);
        tempC.gridx = 2;
        tempC.gridy = 1;
        tempC.anchor = GridBagConstraints.CENTER;
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //purBagPane.setVisible(false);
                bagsCount = bagsCount + 1;
                tempBagsCount.setText(String.valueOf(bagsCount));
                minus.setEnabled(true);
                purchase.setEnabled(true);
            }
        });
        purBagPane.add(add, c);
    }

    // Catalogue popup *Alvin
    private void cataPopup() {
        // Set up catalogue panel, my idea is to have a navigation bar on top of the panel and the rest will be top selling items
        // because of limited time, i think the way we should implement is to use this as top 9 most popular items in last week
        JPanel cataPanel = new JPanel();
        layeredPane.setLayer(cataPanel, 1);
        cataPanel.setBounds(0, 0, 1000, 900);
        cataPanel.setBackground(Color.LIGHT_GRAY);
        layeredPane.add(cataPanel);
        cataPanel.setLayout(new GridBagLayout());

        // Set up items for catalogue
        String[] dataBeer = {"Beer @$26.99", "1.00", "$26.99", "1001"};
        String[] dataBread = {"Bread @$1.97", "1.00", "$1.97", "1002"};
        String[] dataCereals = {"Cereals @$4.77", "1.00", "$4.77", "1003"};
        String[] dataCheese = {"Cheese @$8.27", "1.00", "$8.27", "1004"};
        String[] dataEggs = {"Eggs @$3.68", "1.00", "$3.68", "1005"};
        String[] dataMilk = {"Milk @$5.89", "1.00", "$5.89", "1006"};
        String[] dataProducts = {"Products @$1.98", "1.00", "$1.98", "1007"};
        String[] dataSoda = {"Soda @$2.47", "1.00", "$2.47", "1008"};
        String[] dataSweets = {"Sweets @$0.99", "1.00", "$0.99", "1009"};

        // Set up gridbagconstraints
        GridBagConstraints tempC = new GridBagConstraints();

        // Back to main screen button
        JButton back = new JButton("Back to main screen");
        back.setFont(new Font("Tahoma", Font.PLAIN, 15));
        back.setPreferredSize(new Dimension(200, 50));
        back.setBounds(392, 20, 200, 50);
        // add to panel
        tempC.gridx = 1;
        tempC.gridy = 0;
        tempC.fill = GridBagConstraints.HORIZONTAL;
        tempC.anchor = GridBagConstraints.NORTH;
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cataPanel.setVisible(false);
            }
        });
        cataPanel.add(back, c);

        // Set up items, inside each button listerner should do sth to add to the summary

        // Item 1
        JButton itemCata1 = new JButton();
        Image itemCataPic1 = new ImageIcon("com.autovend.software/img_src/beer.png").getImage();
        itemCata1.setBackground(Color.DARK_GRAY);
        itemCata1.setIcon(new ImageIcon(itemCataPic1));
        itemCata1.setPreferredSize(new Dimension(200, 200));
        itemCata1.setBounds(42, 100, 150, 150);
        // add to panel
        tempC.gridx = 0;
        tempC.gridy = 1;
        itemCata1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                data[dataCount] = dataBeer;
                dataCount++;
                dataModel.addRow(dataBeer);
                updateTable(dataBeer);
                cataPanel.setVisible(false);
            }
        });
        cataPanel.add(itemCata1, c);

        // Item 2
        JButton itemCata2 = new JButton();
        Image itemCataPic2 = new ImageIcon("com.autovend.software/img_src/bread.png").getImage();
        itemCata2.setBackground(Color.DARK_GRAY);
        itemCata2.setIcon(new ImageIcon(itemCataPic2));
        itemCata2.setPreferredSize(new Dimension(200, 200));
        itemCata2.setBounds(417, 100, 150, 150);
        // add to panel
        tempC.gridx = 1;
        tempC.gridy = 1;
        itemCata2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                data[dataCount] = dataBread;
                dataCount++;
                dataModel.addRow(dataBread);
                updateTable(dataBread);
                cataPanel.setVisible(false);
            }
        });
        cataPanel.add(itemCata2, c);

        // Item 3
        JButton itemCata3 = new JButton();
        Image itemCataPic3 = new ImageIcon("com.autovend.software/img_src/cereals.png").getImage();
        itemCata3.setBackground(Color.DARK_GRAY);
        itemCata3.setIcon(new ImageIcon(itemCataPic3));
        itemCata3.setPreferredSize(new Dimension(200, 200));
        itemCata3.setBounds(792, 100, 150, 150);
        // add to panel
        tempC.gridx = 2;
        tempC.gridy = 1;
        itemCata3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                data[dataCount] = dataCereals;
                dataCount++;
                dataModel.addRow(dataCereals);
                updateTable(dataCereals);
                cataPanel.setVisible(false);
            }
        });
        cataPanel.add(itemCata3, c);

        // Item 4
        JButton itemCata4 = new JButton();
        Image itemCataPic4 = new ImageIcon("com.autovend.software/img_src/eggs.png").getImage();
        itemCata4.setBackground(Color.DARK_GRAY);
        itemCata4.setIcon(new ImageIcon(itemCataPic4));
        itemCata4.setPreferredSize(new Dimension(200, 200));
        itemCata4.setBounds(42, 317, 150, 150); //535
        // add to panel
        tempC.gridx = 0;
        tempC.gridy = 2;
        itemCata4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                data[dataCount] = dataEggs;
                dataCount++;
                dataModel.addRow(dataEggs);
                updateTable(dataEggs);
                cataPanel.setVisible(false);
            }
        });
        cataPanel.add(itemCata4, c);

        // Item 5
        JButton itemCata5 = new JButton();
        Image itemCataPic5 = new ImageIcon("com.autovend.software/img_src/milk.png").getImage();
        itemCata5.setBackground(Color.DARK_GRAY);
        itemCata5.setIcon(new ImageIcon(itemCataPic5));
        itemCata5.setPreferredSize(new Dimension(200, 200));
        itemCata5.setBounds(417, 317, 150, 150);
        // add to panel
        tempC.gridx = 1;
        tempC.gridy = 2;
        itemCata5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                data[dataCount] = dataMilk;
                dataCount++;
                dataModel.addRow(dataMilk);
                updateTable(dataMilk);
                cataPanel.setVisible(false);
            }
        });
        cataPanel.add(itemCata5, c);
        // Item 6
        JButton itemCata6 = new JButton();
        Image itemCataPic6 = new ImageIcon("com.autovend.software/img_src/products.png").getImage(); // snacks actually
        itemCata6.setBackground(Color.DARK_GRAY);
        itemCata6.setIcon(new ImageIcon(itemCataPic6));
        itemCata6.setPreferredSize(new Dimension(200, 200));
        itemCata6.setBounds(792, 317, 150, 150);
        // add to panel
        tempC.gridx = 2;
        tempC.gridy = 2;
        itemCata6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                data[dataCount] = dataProducts;
                dataCount++;
                dataModel.addRow(dataProducts);
                updateTable(dataProducts);
                cataPanel.setVisible(false);
            }
        });
        cataPanel.add(itemCata6, c);

        // Item 7
        JButton itemCata7 = new JButton();
        Image itemCataPic7 = new ImageIcon("com.autovend.software/img_src/soda.png").getImage();
        itemCata7.setBackground(Color.DARK_GRAY);
        itemCata7.setIcon(new ImageIcon(itemCataPic7));
        itemCata7.setPreferredSize(new Dimension(200, 200));
        itemCata7.setBounds(42, 535, 150, 150);
        // add to panel
        tempC.gridx = 0;
        tempC.gridy = 2;
        itemCata7.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                data[dataCount] = dataSoda;
                dataCount++;
                dataModel.addRow(dataSoda);
                updateTable(dataSoda);
                cataPanel.setVisible(false);
            }
        });
        cataPanel.add(itemCata7, c);

        // Item 8
        JButton itemCata8 = new JButton();
        Image itemCataPic8 = new ImageIcon("com.autovend.software/img_src/sweets.png").getImage();
        itemCata8.setBackground(Color.DARK_GRAY);
        itemCata8.setIcon(new ImageIcon(itemCataPic8));
        itemCata8.setPreferredSize(new Dimension(200, 200));
        itemCata8.setBounds(417, 535, 150, 150);
        // add to panel
        tempC.gridx = 1;
        tempC.gridy = 2;
        itemCata8.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                data[dataCount] = dataSweets;
                dataCount++;
                dataModel.addRow(dataSweets);
                updateTable(dataSweets);
                cataPanel.setVisible(false);
            }
        });
        cataPanel.add(itemCata8, c);
        // Item 9
        JButton itemCata9 = new JButton();
        Image itemCataPic9 = new ImageIcon("com.autovend.software/img_src/cheese.png").getImage();
        itemCata9.setBackground(Color.DARK_GRAY);
        itemCata9.setIcon(new ImageIcon(itemCataPic9));
        itemCata9.setPreferredSize(new Dimension(200, 200));
        itemCata9.setBounds(792, 535, 150, 150);
        // add to panel
        tempC.gridx = 2;
        tempC.gridy = 2;
        itemCata9.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                data[dataCount] = dataCheese;
                dataCount++;
                dataModel.addRow(dataCheese);
                updateTable(dataCheese);
                cataPanel.setVisible(false);
            }
        });
        cataPanel.add(itemCata9, c);
    }

    private void ownBag() {
        JPanel ownBag = new JPanel();
        layeredPane.setLayer(ownBag, 1);
        ownBag.setBounds(0, 0, 984, 785);
        ownBag.setBackground(Color.LIGHT_GRAY);
        layeredPane.add(ownBag);
        ownBag.setLayout(new GridBagLayout());

        JTextField haveBag = new JTextField("Do you bring your own bag(s) today?");
        haveBag.setFont(new Font("Arial", Font.PLAIN, 40));
        haveBag.setEditable(false);
        haveBag.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        haveBag.setBackground(Color.LIGHT_GRAY);
        haveBag.setBounds(0, 0, 984, 785);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.ipady = 20;
        ownBag.add(haveBag, c);

        JButton yb = new JButton("Yes");
        JButton nb = new JButton("No");
        yb.setFont(new Font("Arial", Font.PLAIN, 40));
        nb.setFont(new Font("Arial", Font.PLAIN, 40));
        yb.setPreferredSize(new Dimension(200, 75));
        nb.setPreferredSize(new Dimension(200, 75));
        c.gridy = 1;
        ownBag.add(yb, c);
        c.gridy = 2;
        ownBag.add(nb, c);
        nb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ownBag.setVisible(false);
            }
        });
        yb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ownBag.setVisible(false);
                ownBagAdded();
            }
        });
    }

    public void ownBagAdded() {
        ownBagAdded = new JPanel();
        layeredPane.setLayer(ownBagAdded, 1);
        ownBagAdded.setBounds(0, 0, 984, 785);
        ownBagAdded.setBackground(Color.LIGHT_GRAY);
        layeredPane.add(ownBagAdded);
        ownBagAdded.setLayout(null);

        JTextArea hadBag = new JTextArea("Please put your bag(s) into bagging area \n   and wait for our attendant to confirm");
        hadBag.setFont(new Font("Arial", Font.PLAIN, 40));
        hadBag.setEditable(false);
        hadBag.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        hadBag.setBackground(Color.LIGHT_GRAY);
        hadBag.setBounds(145, 350, 984, 785);

        ownBagAdded.add(hadBag);

        // Set the oB to be true and wait for attendant to accept
        oB = true;

        if (!oB) {
            ownBagAdded.setVisible(false);
        }

        // Temporary disable these codes
//		checkoutController.addOwnBags();
//		// wait for attendant to confirm and then set visible to false, rn i'm changing it right away
//		checkoutController.AttendantApproved = false;	// this will change after get acceptance from attendant
//		if (checkoutController.AttendantApproved) {
//			ownBagAdded.setVisible(false);
//		}
    }

    private void tapScreen() {

        tapScreenPanel = new JPanel();
        layeredPane.setLayer(tapScreenPanel, 1);
        tapScreenPanel.setBounds(0, 0, 985, 785);
        layeredPane.add(tapScreenPanel);
        tapScreenPanel.setLayout(null);

        screensaver = new JButton("Welcome. \nPlease touch the screen to continue.");
        screensaver.setDisplayedMnemonicIndex(1);
        screensaver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tapScreenPanel.setVisible(false);
            }
        });
        screensaver.setFont(new Font("Arial", Font.PLAIN, 40));
        screensaver.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        screensaver.setBounds(0, 0, 984, 785);
        screensaver.setContentAreaFilled(false);
        tapScreenPanel.add(screensaver);
    }

    private void setUpLanguage() {
        languageBox = new JComboBox<String>();
        languageBox.setModel(new DefaultComboBoxModel<String>(new String[]{"English", "French", "Spanish"}));
        languageBox.setFont(new Font("Tahoma", Font.PLAIN, 15));
        languageBox.setBounds(10, 24, 115, 42);
        secondaryPanel.add(languageBox);
        languageBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (languageBox.getSelectedIndex() == 0) {
                    helpButton.setText("Need help?");
                    audioButton.setText("Text-to-speech");
                }
                if (languageBox.getSelectedIndex() == 1) {
                    helpButton.setText("Besoin d'aide?");
                    audioButton.setText("texte pour parler");
                }
                if (languageBox.getSelectedIndex() == 2) {
                    helpButton.setText("¿Necesitas ayuda?");
                    audioButton.setText("texto a voz");
                }
            }
        });
    }

    private void setUpAudio() {
        audioButton = new JButton("Audio");
        audioButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        audioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                audioButtonOn = !audioButtonOn;
                System.out.println("Text-to-speech is: " + audioButtonOn);
            }
        });
        audioButton.setBounds(135, 24, 149, 42);
        secondaryPanel.add(audioButton);
    }

    private void setUpHelp() {
        helpButton = new JButton("Help");
        helpButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Calling attendant");
            }
        });
        helpButton.setBounds(799, 25, 160, 41);
        secondaryPanel.add(helpButton);
    }

    private void setUpPayment(SelfCheckoutStation cStation) {
        paymentButton = new JButton("Continue to payment >>>");
        paymentButton.setFont(new Font("Tahoma", Font.PLAIN, 17));
        paymentButton.setBounds(564, 703, 393, 40);
        paymentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paymentTableModel.addRow(new Object[]{"Item Cost", "Item Count", "Item Cost"});
                mainPanel.setVisible(false);
                PaymentScreen s = new PaymentScreen(cStation, paymentTableModel, cartValue);
            }
        });
        mainPanel.add(paymentButton);
    }

    // Total cart value is $50 rn, will update it after fixing the setuppaymenttable
    private void setUpPaymentTotal(String cartValue) {
        JPanel totalPane = new JPanel();
        totalPane.setLayout(new GridLayout(1, 2));
        totalPane.setBounds(564, 641, 393, 50);
        Border blackline = BorderFactory.createLineBorder(Color.black);
        totalPane.setBorder(blackline);
        mainPanel.add(totalPane);

        JTextField tt = new JTextField("  Total:");
        tt.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tt.setBorder(null);
        totalPane.add(tt);

        JTextField costs = new JTextField(cartValue);
        costs.setFont(new Font("Tahoma", Font.PLAIN, 15));
        costs.setBorder(null);
        costs.setHorizontalAlignment(SwingConstants.RIGHT);
        totalPane.add(costs);
    }

    // Still fixing
    private void setUpPaymentTable() {
        scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(564, 45, 393, 596);
        mainPanel.add(scrollPane);

        // Setup jtable, waiting to connect to software

        table = new JTable(paymentTableModel);
//		paymentTableModel = (DefaultTableModel) table.getModel();

        scrollPane.setViewportView(table);
        // count total cart value and pass to the setuppaymenttotal
        // These should use the software when connected imo
        setUpPaymentTotal("$0.00  ");
    }

    private void digitsFailed() {
        tapScreenPanel = new JPanel();
        layeredPane.setLayer(tapScreenPanel, 1);
        tapScreenPanel.setBounds(0, 0, 985, 785);
        layeredPane.add(tapScreenPanel);
        tapScreenPanel.setLayout(null);

        screensaver = new JButton("Your number is not valid. \nTap here to try again");
        screensaver.setDisplayedMnemonicIndex(1);
        screensaver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tapScreenPanel.setVisible(false);
            }
        });
        screensaver.setFont(new Font("Arial", Font.PLAIN, 40));
        screensaver.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        screensaver.setBounds(0, 0, 984, 785);
        screensaver.setContentAreaFilled(false);
        tapScreenPanel.add(screensaver);
    }

    private void setUpNumericKeyboard(int checkDigits) {

        String[] dataBeer = {"Beer @$26.99", "1.00", "$26.99", "1001"};
        String[] dataBread = {"Bread @$1.97", "1.00", "$1.97", "1002"};
        String[] dataCereals = {"Cereals @$4.77", "1.00", "$4.77", "1003"};
        String[] dataCheese = {"Cheese @$8.27", "1.00", "$8.27", "1004"};
        String[] dataEggs = {"Eggs @$3.68", "1.00", "$3.68", "1005"};
        String[] dataMilk = {"Milk @$5.89", "1.00", "$5.89", "1006"};
        String[] dataProducts = {"Products @$1.98", "1.00", "$1.98", "1007"};
        String[] dataSoda = {"Soda @$2.47", "1.00", "$2.47", "1008"};
        String[] dataSweets = {"Sweets @$0.99", "1.00", "$0.99", "1009"};


        tempCount = 0;

        keyboardPanel = new JPanel();
        keyboardPanel.setOpaque(true);
        layeredPane.setLayer(keyboardPanel, 1);
        keyboardPanel.setBounds(350, 144, 290, 465);
        layeredPane.add(keyboardPanel);
        keyboardPanel.setLayout(null);

        keyboardText = "";
        keyboardTextField = new JTextField(keyboardText);
        keyboardTextField.setEditable(false);
        keyboardTextField.setFocusable(false);
        keyboardTextField.setBounds(65, 10, 215, 44);
        keyboardTextField.setBackground(Color.WHITE);
        keyboardTextField.setFont(new Font("Tahoma", Font.PLAIN, 15));
        keyboardPanel.add(keyboardTextField);
        keyboardTextField.setColumns(10);

        //Add the buttons
        keyZero = new JButton("0");
        keyZero.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        keyZero.setFont(new Font("Tahoma", Font.PLAIN, 25));
        keyZero.setBounds(0, 65, 90, 90);
        keyZero.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                keyboardText += "0";
                keyboardTextField.setText(keyboardText);
                tempCount += 1;
            }
        });
        keyboardPanel.add(keyZero);

        keyOne = new JButton("1");
        keyOne.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        keyOne.setFont(new Font("Tahoma", Font.PLAIN, 25));
        keyOne.setBounds(100, 65, 90, 90);
        keyOne.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                keyboardText += "1";
                keyboardTextField.setText(keyboardText);
                tempCount += 1;
            }
        });
        keyboardPanel.add(keyOne);

        keyTwo = new JButton("2");
        keyTwo.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        keyTwo.setFont(new Font("Tahoma", Font.PLAIN, 25));
        keyTwo.setBounds(200, 65, 90, 90);
        keyTwo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                keyboardText += "2";
                keyboardTextField.setText(keyboardText);
                tempCount += 1;
            }
        });
        keyboardPanel.add(keyTwo);

        keyThree = new JButton("3");
        keyThree.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        keyThree.setFont(new Font("Tahoma", Font.PLAIN, 25));
        keyThree.setBounds(0, 165, 90, 90);
        keyThree.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                keyboardText += "3";
                keyboardTextField.setText(keyboardText);
                tempCount += 1;
            }
        });
        keyboardPanel.add(keyThree);

        keyFour = new JButton("4");
        keyFour.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        keyFour.setFont(new Font("Tahoma", Font.PLAIN, 25));
        keyFour.setBounds(100, 165, 90, 90);
        keyFour.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                keyboardText += "4";
                keyboardTextField.setText(keyboardText);
                tempCount += 1;
            }
        });
        keyboardPanel.add(keyFour);

        keyFive = new JButton("5");
        keyFive.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        keyFive.setFont(new Font("Tahoma", Font.PLAIN, 25));
        keyFive.setBounds(200, 165, 90, 90);
        keyFive.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                keyboardText += "5";
                keyboardTextField.setText(keyboardText);
                tempCount += 1;
            }
        });
        keyboardPanel.add(keyFive);

        keySix = new JButton("6");
        keySix.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        keySix.setFont(new Font("Tahoma", Font.PLAIN, 25));
        keySix.setBounds(0, 265, 90, 90);
        keySix.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                keyboardText += "6";
                keyboardTextField.setText(keyboardText);
                tempCount += 1;
            }
        });
        keyboardPanel.add(keySix);

        keySeven = new JButton("7");
        keySeven.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        keySeven.setFont(new Font("Tahoma", Font.PLAIN, 25));
        keySeven.setBounds(100, 265, 90, 90);
        keySeven.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                keyboardText += "7";
                keyboardTextField.setText(keyboardText);
                tempCount += 1;
            }
        });
        keyboardPanel.add(keySeven);

        keyEight = new JButton("8");
        keyEight.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        keyEight.setFont(new Font("Tahoma", Font.PLAIN, 25));
        keyEight.setBounds(200, 265, 90, 90);
        keyEight.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                keyboardText += "8";
                keyboardTextField.setText(keyboardText);
                tempCount += 1;
            }
        });
        keyboardPanel.add(keyEight);

        keyNine = new JButton("9");
        keyNine.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        keyNine.setFont(new Font("Tahoma", Font.PLAIN, 25));
        keyNine.setBounds(0, 365, 90, 90);
        keyNine.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                keyboardText += "9";
                keyboardTextField.setText(keyboardText);
                tempCount += 1;
            }
        });
        keyboardPanel.add(keyNine);

        // Would probably call Membership and PLU searching here
        keyEnter = new JButton("Enter");
        keyEnter.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        keyEnter.setFont(new Font("Tahoma", Font.PLAIN, 25));
        keyEnter.setBounds(100, 365, 190, 90);
        keyEnter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Value" + keyboardText);
                ArrayList<Numeral> numerals = new ArrayList<Numeral>();


                if (keyboardText.equals(dataBeer[3])) {
                    data[dataCount] = dataBeer;
                    dataCount++;
                    dataModel.addRow(dataBeer);
                    updateTable(dataBeer);
                }

                if (keyboardText.equals(dataBread[3])) {
                    data[dataCount] = dataBread;
                    dataCount++;
                    dataModel.addRow(dataBread);
                    updateTable(dataBread);
                }

                if (keyboardText.equals(dataCereals[3])) {
                    data[dataCount] = dataCereals;
                    dataCount++;
                    dataModel.addRow(dataCereals);
                    updateTable(dataCereals);
                }

                if (keyboardText.equals(dataCheese[3])) {
                    data[dataCount] = dataCheese;
                    dataCount++;
                    dataModel.addRow(dataCheese);
                    updateTable(dataCheese);
                }

                if (keyboardText.equals(dataEggs[3])) {
                    data[dataCount] = dataEggs;
                    dataCount++;
                    dataModel.addRow(dataEggs);
                    updateTable(dataEggs);
                }

                if (keyboardText.equals(dataMilk[3])) {
                    data[dataCount] = dataMilk;
                    dataCount++;
                    dataModel.addRow(dataMilk);
                    updateTable(dataMilk);
                }

                if (keyboardText.equals(dataProducts[3])) {
                    data[dataCount] = dataProducts;
                    dataCount++;
                    dataModel.addRow(dataProducts);
                    updateTable(dataProducts);
                }

                if (keyboardText.equals(dataSoda[3])) {
                    data[dataCount] = dataSoda;
                    dataCount++;
                    dataModel.addRow(dataSoda);
                    updateTable(dataSoda);
                }

                if (keyboardText.equals(dataSweets[3])) {
                    data[dataCount] = dataSweets;
                    dataCount++;
                    dataModel.addRow(dataSweets);
                    updateTable(dataSweets);
                }


                // If it's failed, pop up a screen, otherwise selectedmembership is true, at this moment
                if (checkDigits == 1) {
                    if (tempCount != 12) {
                        digitsFailed();
                    } else {
                        selectedMembership = true;
                    }
                }

                // Not sure about this part

                if (selectedPLU) {
                    for (char c : keyboardText.toCharArray()) {
                        numerals.add(Numeral.valueOf((byte) Character.getNumericValue(c)));
                    }
                    Numeral[] code = numerals.toArray(new Numeral[numerals.size()]);
                    // Insert PLU method here:
                    // PLUmethod(code)
                } else if (selectedMembership) {
                    // insert Membership method here
                    // Enter membership(keyboardText)
                    //checkoutController.inputMembershipNumber();
                    //membershipCardController.updateMembershipStatus();
                }
                System.out.println("PLU: " + selectedPLU);
                System.out.println("Membership: " + selectedMembership);
                keyboardText = "";
                keyboardTextField.setText(keyboardText);
                closeKeyboard();
            }
        });


        keyboardPanel.add(keyEnter);

        keyExit = new JButton("X");
        keyExit.setOpaque(true);
        keyExit.setBackground(new Color(255, 0, 0));
        keyExit.setForeground(new Color(255, 255, 255));
        keyExit.setFont(new Font("Tahoma", Font.BOLD, 16));
        keyExit.setBounds(10, 10, 45, 45);
        keyExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedPLU = false;
                selectedMembership = false;
                keyboardText = "";
                keyboardTextField.setText(keyboardText);
                closeKeyboard();
            }
        });
        keyboardPanel.add(keyExit);

        keyboardPanel.setVisible(false);


    }

    /**
     * Helper method to lock buttons while the keyboard is up
     *
     * @param i
     */
    private void openKeyboard(int i) {
        if (i == 1) {
            setUpNumericKeyboard(i);
        }
        keyboardPanel.setVisible(true);
        PLUTextButton.setEnabled(false);
        itemCata.setEnabled(false);
        memberTextButton.setEnabled(false);
        purchaseBagsButton.setEnabled(false);
        removeItemButton.setEnabled(false);
        paymentButton.setEnabled(false);

    }

    /**
     * Helper method to unlock buttons when the user is done with the keyboard
     */
    private void closeKeyboard() {
        keyboardPanel.setVisible(false);
        PLUTextButton.setEnabled(true);
        itemCata.setEnabled(true);
        memberTextButton.setEnabled(true);
        purchaseBagsButton.setEnabled(true);
        removeItemButton.setEnabled(true);
        paymentButton.setEnabled(true);
    }

    public void updateTable(String[] item) {
        paymentTableModel.addRow(new Object[]{item[0], item[1], item[2]});
        dataNeo[dataCount - 1] = item[0];
//		System.out.println("Item added: " + item[0]);
        String tmp = item[2].replace("$", "");
        // count total cart value and pass to the setuppaymenttotal
        // These should use the software when connected imo
        cartValue = cartValue + Double.valueOf(tmp);
        DecimalFormat cartValueFormatted = new DecimalFormat("#.00");
        setUpPaymentTotal("$" + cartValueFormatted.format(cartValue) + "  ");

        // These are for test if data is keep track of the order
//		System.out.println(dataCount);
//		System.out.println(data[dataCount-1][2]);
    }
}
