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

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Remove {

    // remove handles the GUI for the remove an item for the self checkout station. 

    private final JPanel removePanel;
    private final JLabel label;
    // all the components that are added to the screen
    JFrame touchScreenFrame;
    private JScrollPane scrollPane;

    public Remove(SelfCheckoutStation cStation, String[] items, int count) {

        this.touchScreenFrame = cStation.screen.getFrame();
        this.touchScreenFrame.setExtendedState(JFrame.NORMAL);
        this.touchScreenFrame.setSize(1000, 900);
        this.touchScreenFrame.setBounds(0, 0, 1000, 900);
        this.touchScreenFrame.setResizable(true);
        this.touchScreenFrame.getContentPane().setBackground(new Color(247, 247, 247));

        removePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 150));
        removePanel.setBounds(0, 0, 984, 785);
        removePanel.setBackground(new Color(247, 247, 247));
        Border paddingBorder = BorderFactory.createEmptyBorder(20, 50, 20, 50);
        Border border = BorderFactory.createLineBorder(new Color(0, 0, 0), 2);
        removePanel.setBorder(BorderFactory.createCompoundBorder(border, paddingBorder));

        label = new JLabel("Removing item! Attendant is taking care of it.", JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 26));
        label.setForeground(new Color(0, 102, 204));
        removePanel.add(label, BorderLayout.CENTER);

        removePanel.setVisible(false);
        checkbox(items, count);

        this.touchScreenFrame.getContentPane().add(removePanel);
        this.touchScreenFrame.setVisible(true);
        this.touchScreenFrame.setResizable(false);
    }

    public void checkSetup(String tmp) {
        JCheckBox tmpC = new JCheckBox(tmp);
        tmpC.setFont(new Font("Segoe UI", Font.PLAIN, 26));
        scrollPane.getViewport().add(tmpC);
    }

    public void checkbox(String[] items, int count) {
        JPanel checkP = new JPanel();//(new GridLayout(1, 2));
        checkP.setBounds(0, 0, 984, 785);

        scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);//HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(300, 500));
        scrollPane.setAlignmentX(count);
        scrollPane.setBounds(0, 0, 500, 785);    //984, 785
        for (int i = 0; i < count; i++) {
            checkSetup(items[i]);
            System.out.println(items[i]);
        }
        checkP.add(scrollPane);


//		String[] tmp = new String[count];
//		for (int i = 0; i < count; i++) {
//			tmp[i] = items[i]; 
//		}
//		JList<JCheckBox> l = new JList<JCheckBox>();
//		l.setFont(new Font("Segoe UI", Font.PLAIN, 26));
//		JCheckBox tmp1 = new JCheckBox(items[0]);
//		l.add(tmp1, 0);
//		checkP.add(l);
//		checkP.add(tmp1);
        //scrollPane.add(l);
//		for (int i = 0; i <= count; i++) {
//			JCheckBox c = new JCheckBox(items[i]); 
//			l.add(c, i);
//			c.setFont(new Font("Segoe UI", Font.PLAIN, 26));
//			// scrollPane.add(l);
//		}
//		scrollPane.add(l);

        JButton cf = new JButton("Remove an Item");
        cf.setFont(new Font("Tahoma", Font.PLAIN, 15));
        cf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkP.setVisible(false);
                removePanel.setVisible(true);
            }
        });
        cf.setBounds(142, 342, 200, 100);
        checkP.add(cf);

        touchScreenFrame.add(checkP);

    }

//    public static void main(String[] args) {
//    	SelfCheckoutStation cStation = new SelfCheckoutStation(null, null, null, 0, 0);
//        new Remove(cStation);
//    }
}

