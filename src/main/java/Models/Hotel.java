package Models;

/**
 * Model class representing a hotel in the booking system.
 * Contains basic information about a hotel such as name, contact details,
 * quality rating (stars), location, and a brief description.
 */
public class Hotel {
    /** The name of the hotel */
    private String name;

    /** Contact phone number for the hotel */
    private String phoneNumber;

    /** Quality rating of the hotel (1-5 stars) */
    private int stars;

    /** City where the hotel is located */
    private String city;

    /** Brief description of the hotel and its amenities */
    private String description;

    /**
     * Constructor to create a new Hotel object with all required information
     * @param name The name of the hotel
     * @param phoneNumber Contact phone number
     * @param stars Quality rating (1-5)
     * @param city Location city
     * @param description Brief description of the hotel
     */
    public Hotel (String name, String phoneNumber, int stars, String city, String description) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.stars = stars;
        this.city = city;
        this.description = description;
    }

    /**
     * Gets the name of the hotel
     * @return The hotel name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the contact phone number of the hotel
     * @return The hotel's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Gets the quality rating of the hotel
     * @return The star rating (1-5)
     */
    public int getStars() {
        return stars;
    }

    /**
     * Gets the city where the hotel is located
     * @return The hotel's city
     */
    public String getCity() {
        return city;
    }

    /**
     * Gets the description of the hotel
     * @return A brief description of the hotel and its amenities
     */
    public String getDescription() {
        return description;
    }
}
