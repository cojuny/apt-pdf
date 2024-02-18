package com.searcher;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class DataModel {
    private String data;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public DataModel() {
        this.data = "";
    }

    public void setData(String newData) {
        String oldData = this.data;
        this.data = newData;
        // Notify listeners about the change
        pcs.firePropertyChange("data", oldData, newData);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
