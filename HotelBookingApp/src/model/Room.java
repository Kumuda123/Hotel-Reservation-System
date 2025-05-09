package model;

import java.time.LocalDate;
import java.util.*;
import java.time.temporal.ChronoUnit;

public class Room {
    private int roomNumber;
    private String type;
    private double price;
    private String state;
    private String hotelName;
    private String address;
    private String phone;
    private List<Booking> bookings;

    public Room(int roomNumber, String type, double price, String state, String hotelName, String address, String phone) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.state = state;
        this.hotelName = hotelName;
        this.address = address;
        this.phone = phone;
        this.bookings = new ArrayList<>();
    }

    public int getRoomNumber() { return roomNumber; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public String getState() { return state; }
    public String getHotelName() { return hotelName; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }

    public boolean isAvailable(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn != null && checkOut != null && ChronoUnit.MONTHS.between(checkIn, checkOut) > 3) {
            return false; // Cannot book for more than 3 months
        }
        
        for (Booking booking : bookings) {
            if (!(checkOut.isEqual(booking.getCheckIn()) || checkIn.isEqual(booking.getCheckOut()) ||
                  checkOut.isBefore(booking.getCheckIn()) || checkIn.isAfter(booking.getCheckOut()))) {
                return false; // Overlap
            }
        }
        return true;
    }

    // Add booking by details
    public void addBooking(Room room, LocalDate checkIn, LocalDate checkOut) {
        Booking booking = new Booking(checkIn, checkOut, room);
        bookings.add(booking);
    }

    // Add existing booking
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public List<Booking> getMyBookings() {
        return bookings;
    }

    public void book(LocalDate checkIn, LocalDate checkOut) {
        bookings.add(new Booking(checkIn, checkOut, this));
    }
}
