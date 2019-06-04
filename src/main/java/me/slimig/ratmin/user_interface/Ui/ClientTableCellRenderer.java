package me.slimig.ratmin.user_interface.Ui;


import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;


public class ClientTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = -5065328673173404037L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof JLabel) {
            JLabel label = (JLabel) value;
            label.setOpaque(true);
            label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            label.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            return label;
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}