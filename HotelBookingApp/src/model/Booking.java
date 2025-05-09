package model;

import java.time.LocalDate;

public class Booking {
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Room room;

    public Booking(LocalDate checkIn, LocalDate checkOut, Room room) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.room = room;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public Room getRoom() {
        return room;
    }
}
