package com.zabojcampula.vacobul;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StatsForm extends JFrame implements ActionListener {

    private String info;

    public StatsForm(String info) {
        this.info = info;
        CreateUI();
        setVisible(true);
    }

    private void CreateUI() {

        BoxLayout layout = new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS);
        getContentPane().setLayout(layout);
        JPanel panel1 = new JPanel();
        add(panel1);
        panel1.setLayout(new GridLayout());

        panel1.add(new JLabel(info));
        JButton b;
        b = new JButton("Close");
        b.addActionListener(this);
        panel1.add(b);

        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
