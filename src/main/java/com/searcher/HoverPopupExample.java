package com.searcher;

import javax.swing.*;
import java.awt.event.*;

public class HoverPopupExample {
    public static void main(String[] args) {
        // Create and set up the window.
        JFrame frame = new JFrame("Hover Popup Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        // Create a button
        JButton button = new JButton("Hover over me!");
        
        // Create a popup menu but don't attach it to anything.
        JPopupMenu popup = new JPopupMenu();
        popup.add(new JLabel("This is the hover message!"));

        // Add mouse listener to the button
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Show popup below the mouse cursor
                popup.show(e.getComponent(), e.getX(), e.getY() + e.getComponent().getHeight());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Hide the popup when the mouse exits the button
                popup.setVisible(false);
            }
        });

        // Add the button to the frame
        frame.getContentPane().add(button);

        // Display the window.
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
    }
}
