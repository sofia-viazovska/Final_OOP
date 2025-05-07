package Models;

import java.time.LocalDate;

public class Booking {
    private String id;
    private User user;
    private Hotel hotel;
    private Room room;
    private LocalDate checkIn;
    private LocalDate checkOut;

    public Booking(String id, User user, Hotel hotel, Room room, LocalDate checkIn, LocalDate checkOut) {
        this.id = id;
        this.user = user;
        this.hotel = hotel;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public String outputBooking(Booking booking) {
        return "Booking ID: " + booking.id + "\n" +
                "User: " + booking.user.getNickName() + "\n" +
                "Hotel: " + booking.hotel.getName() + "\n" +
                "Check-in: " + booking.checkIn + "\n" +
                "Check-out: " + booking.checkOut;
    }
}
