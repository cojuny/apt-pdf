package com.searcher;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DataChangeListener implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("data".equals(evt.getPropertyName())) {
            System.out.println("Data changed from " + evt.getOldValue() + " to " + evt.getNewValue());
        }
    }

    public static void main(String[] args) {
        DataModel model = new DataModel();
        DataChangeListener listener = new DataChangeListener();
        
        // Register the listener with the model
        model.addPropertyChangeListener(listener);
        
        // Simulate changing the data
        model.setData("Hello, World!");
        // Output: Data changed from  to Hello, World!
    }
}
