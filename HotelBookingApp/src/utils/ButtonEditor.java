package utils;

import java.awt.Component;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import hotel.HotelBookingApp;
import hotel.UserManager;
import model.Room;
import model.User;

public class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private boolean isPushed;
    private DefaultTableModel model;
    private int row;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private User user;
    private JTable table;
    private UserManager userManager;
    private HotelBookingApp hotelBookingApp;

    public ButtonEditor(JCheckBox checkBox, ArrayList<Room> rooms, DefaultTableModel model,
                        LocalDate checkIn, LocalDate checkOut,
                        User user, UserManager userManager, HotelBookingApp hotelBookingApp) {
        super(checkBox);
        this.model = model;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.user = user;
        this.userManager = userManager;
        this.hotelBookingApp = hotelBookingApp;
        this.button = new JButton("Book");
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        this.row = row;
        button.setText("Book");
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            if (!userManager.isLoggedIn()) {
                JOptionPane.showMessageDialog(button, "You must be logged in to make a booking.");
                hotelBookingApp.showLoginPanel();
                return "Book";
            }

            int modelRow = table.convertRowIndexToModel(row);
            int roomNumber = (int) model.getValueAt(modelRow, 0);
            Room room = findRoomByNumber(roomNumber);

            if (room == null) {
                JOptionPane.showMessageDialog(button, "Room not found.");
                return "Book";
            }

            if (checkIn == null || checkOut == null || checkOut.isBefore(checkIn)) {
                JOptionPane.showMessageDialog(button, "Invalid check-in or check-out dates.");
                return "Book";
            }

            if (ChronoUnit.MONTHS.between(checkIn, checkOut) > 3) {
                JOptionPane.showMessageDialog(button, "Booking cannot exceed 3 months.");
                return "Book";
            }

            User user = userManager.getCurrentUser();

            if (room.isAvailable(checkIn, checkOut)) {
                boolean success = userManager.storeBooking(user.getUsername(), room, checkIn, checkOut);
                if (success) {
                    room.book(checkIn, checkOut);
                    user.addBooking(room, checkIn, checkOut);
                    model.setValueAt("No", modelRow, 4);  // Update availability column
                    JOptionPane.showMessageDialog(button,
                        "Room " + room.getRoomNumber() + " successfully booked!\n" +
                        "Check-in: " + checkIn + "\nCheck-out: " + checkOut);
                } else {
                    JOptionPane.showMessageDialog(button, "Booking failed. Room may already be booked.");
                }
            } else {
                JOptionPane.showMessageDialog(button, "Room " + room.getRoomNumber() + " is already booked.");
            }
        }

        isPushed = false;
        return "Book";
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    private Room findRoomByNumber(int number) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:hotel_booking.db")) {
            String sql = "SELECT * FROM rooms WHERE room_number = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, number);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String type = rs.getString("type");
                        double price = rs.getDouble("price");
                        String state = rs.getString("state");
                        String hotelName = rs.getString("hotel_name");
                        String address = rs.getString("address");
                        String phone = rs.getString("phone");
                        return new Room(number, type, price, state, hotelName, address, phone);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
