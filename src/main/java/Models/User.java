package Models;

public class User {
    private String email, userName;
    private String password;
    private String realName;
    private String surname;
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

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
