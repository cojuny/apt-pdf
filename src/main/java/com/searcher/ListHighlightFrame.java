package com.searcher;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ListHighlightFrame extends JFrame {
    private JList<String> myList;
    private JButton changeBackgroundButton;
    private MyCellRenderer cellRenderer;

    public ListHighlightFrame() {
        setTitle("JList Item Highlight Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        // Initialize the JList with some data
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addElement("Item 1");
        model.addElement("Item 2");
        model.addElement("Item 3");
        myList = new JList<>(model);

        // Initialize and set the custom cell renderer
        cellRenderer = new MyCellRenderer();
        myList.setCellRenderer(cellRenderer);

        // Add the JList to a JScrollPane and add it to the frame
        add(new JScrollPane(myList));

        // Initialize the button and add an action listener to it
        changeBackgroundButton = new JButton("Change Background");
        changeBackgroundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = myList.getSelectedIndex();
                if (selectedIndex != -1) {
                    cellRenderer.addIndex(selectedIndex);
                    myList.repaint(); // Repaint the list to update the cell rendering
                }
            }
        });
        add(changeBackgroundButton);

        pack();
    }

    // Custom cell renderer class
    private class MyCellRenderer extends DefaultListCellRenderer {
        private final List<Integer> redBackgroundIndices = new ArrayList<>();

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            c.setBackground(redBackgroundIndices.contains(index) ? Color.RED : Color.WHITE);
            return c;
        }

        public void addIndex(int index) {
            if (!redBackgroundIndices.contains(index)) {
                redBackgroundIndices.add(index);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ListHighlightFrame().setVisible(true));
    }
}