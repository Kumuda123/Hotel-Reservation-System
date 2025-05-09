package utils;

import java.awt.Component;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import hotel.UserManager;

public class DeleteButtonEditor extends DefaultCellEditor {
    private JButton button = new JButton("Delete");
    private boolean isPushed;
    private DefaultTableModel model;
    private JTable table;
    private UserManager userManager;

    public DeleteButtonEditor(JCheckBox checkBox,
                              DefaultTableModel model,
                              UserManager userManager) {
        super(checkBox);
        this.model = model;
        this.userManager = userManager;
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row, int col) {
        this.table = table;
        this.isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            int viewRow = table.getSelectedRow();
            if (viewRow >= 0) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                int bookingId = (int) model.getValueAt(modelRow, 0);

                if (userManager.deleteBooking(bookingId)) {
                    // Defer removal to avoid conflict during editing lifecycle
                    SwingUtilities.invokeLater(() -> {
                        model.removeRow(modelRow);
                        JOptionPane.showMessageDialog(button,
                            "Booking deleted successfully.");
                    });
                } else {
                    JOptionPane.showMessageDialog(button,
                        "Failed to delete booking.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        isPushed = false;
        return "Delete";
    }


    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}
