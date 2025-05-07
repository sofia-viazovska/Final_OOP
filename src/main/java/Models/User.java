package Models;

public class User {
    private String email, userName;
    private String password;
    private Booking cart;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        String[] parts = this.email.split("@");
        this.userName = parts[0];
    }

    public Booking getBookings() {
        return cart;
    }
    public void addBooking(Booking booking) {
        cart = booking;
    }

    public String getNickName() {
        return userName;
    }
}
