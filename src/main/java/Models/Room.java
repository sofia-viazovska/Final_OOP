package Models;

/**
 * Model class representing a room in a hotel.
 * Contains information about the room type, price, availability,
 * and a reference to the hotel it belongs to.
 */
public class Room{
    /** The hotel this room belongs to */
    private Hotel hotel;

    /** The type/category of room (e.g., Standard, Deluxe, Suite) */
    private String type;

    /** The price per day for staying in this room */
    private int pricePerDay;

    /** The number of rooms of this type that are available */
    private int available;

    /** Description of the room and its features */
    private String description;

    /**
     * Constructor to create a new Room object with all required information
     * @param hotel The hotel this room belongs to
     * @param type The type/category of the room
     * @param pricePerDay The daily rate for the room
     * @param available The number of rooms of this type available
     * @param description Brief description of the room and its features
     */
    public Room(Hotel hotel, String type, int pricePerDay, int available, String description) {
        this.hotel = hotel;
        this.type = type;
        this.pricePerDay = pricePerDay;
        this.available = available;
        this.description = description;
    }

    /**
     * Gets the hotel this room belongs to
     * @return The associated hotel object
     */
    public Hotel getHotel() {
        return hotel;
    }

    /**
     * Gets the type/category of the room
     * @return The room type (e.g., Standard, Deluxe, Suite)
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the daily price for the room
     * @return The price per day in dollars
     */
    public int getPrice() {
        return pricePerDay;
    }

    /**
     * Gets the number of rooms of this type that are available
     * @return The availability count
     */
    public int getAvailable() {
        return available;
    }

    /**
     * Sets the number of rooms of this type that are available
     * @param available The new availability count
     */
    public void setAvailable(int available) {
        this.available = available;
    }

    /**
     * Gets the description of the room
     * @return A brief description of the room and its features
     */
    public String getDescription() {
        return description;
    }
}
