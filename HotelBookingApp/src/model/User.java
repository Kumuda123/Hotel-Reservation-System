package model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class User {
    private String username;
    private String password;
    private String name;
    private String funFact;
    private List<Booking> bookings;

    public User(String username, String password, String name, String funFact) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.funFact = funFact;
        this.bookings = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getFunFact() {
        return funFact;
    }

    public void addBooking(Room room, LocalDate checkIn, LocalDate checkOut) {
        Booking booking = new Booking(checkIn, checkOut, room);
        bookings.add(booking);
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public List<Booking> getMyBookings() {
        return bookings;
    }
}
