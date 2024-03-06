package com.searcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ListSelectionExample extends JFrame {
    public ListSelectionExample() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLayout(new FlowLayout());

        JList<String> myList = new JList<>(new String[]{"Item 1", "Item 2", "Item 3"});
        myList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // This line is actually the default
        add(new JScrollPane(myList));

        JButton button = new JButton("Get Selected Item Index");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = myList.getSelectedIndex();
                System.out.println("Selected item index: " + selectedIndex);
            }
        });
        add(button);

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ListSelectionExample().setVisible(true));
    }
}
