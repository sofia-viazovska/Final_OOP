package Controllers;

import Models.Hotel;
import Models.Room;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class responsible for hotel search functionality.
 * Manages the display of hotels and their rooms based on search criteria.
 * Provides methods for finding hotels by city and displaying available rooms.
 */
public class HotelFindController {
    /** Static list to store all available hotels */
    private static List<Hotel> hotels = new ArrayList<>();

    /** Static list to store all available rooms */
    private static List<Room> rooms = new ArrayList<>();

    /** Reference to the RoomBookingController for booking rooms */
    private RoomBookingController bookingController;

    /**
     * Default constructor
     */
    public HotelFindController() {
        this.bookingController = new RoomBookingController();
    }

    /**
     * Constructor with RoomBookingController
     * @param bookingController The RoomBookingController to use for booking rooms
     */
    public HotelFindController(RoomBookingController bookingController) {
        this.bookingController = bookingController;
    }

    /**
     * Adds a hotel to the system's hotel database
     * @param hotel The hotel object to be added
     */
    public static void addHotel(Hotel hotel) {
        hotels.add(hotel);
    }

    /**
     * Adds a room to the system's room database
     * @param room The room object to be added
     */
    public static void addRoom(Room room) {
        rooms.add(room);
    }

    /**
     * Finds hotels in a specific city
     * @param city The name of the city to search for (case insensitive)
     * @return A list of hotels located in the specified city
     */
    public List<Hotel> findHotelsByCity(String city) {
        return hotels.stream()
                .filter(hotel -> hotel.getCity().equalsIgnoreCase(city))
                .collect(Collectors.toList());
    }

    /**
     * Displays hotels in the specified city in the provided container
     * @param city The city to search for hotels
     * @param resultsContainer The VBox container where hotel results will be displayed
     * @param checkInDate The selected check-in date
     * @param checkOutDate The selected check-out date
     */
    public void displayHotelsInCity(String city, VBox resultsContainer, LocalDate checkInDate, LocalDate checkOutDate) {
        // Clear previous results
        resultsContainer.getChildren().clear();

        // Find hotels in the specified city
        List<Hotel> foundHotels = findHotelsByCity(city);

        if (foundHotels.isEmpty()) {
            // Display message when no hotels are found
            Label noHotelsLabel = new Label("No hotels available");
            resultsContainer.getChildren().add(noHotelsLabel);
        } else {
            // Display each found hotel with a View button
            for (Hotel hotel : foundHotels) {
                // Create a horizontal box for hotel information and button
                HBox hotelBox = new HBox(10);
                hotelBox.setPadding(new Insets(5));

                // Create a vertical box for hotel details
                VBox hotelInfo = new VBox(5);
                Label hotelLabel = new Label(hotel.getName() + " - " + hotel.getStars() + " stars");
                Label hotelDesc = new Label(hotel.getDescription());
                hotelInfo.getChildren().addAll(hotelLabel, hotelDesc);

                // Create View button with action to display rooms
                Button viewButton = new Button("View");
                viewButton.setOnAction(e -> displayRoomsForHotel(hotel, resultsContainer, checkInDate, checkOutDate));

                // Add hotel info and button to the hotel box
                hotelBox.getChildren().addAll(hotelInfo, viewButton);
                resultsContainer.getChildren().add(hotelBox);
            }
        }
    }

    /**
     * Finds rooms associated with a specific hotel
     * @param hotel The hotel to find rooms for
     * @return A list of rooms belonging to the specified hotel
     */
    public List<Room> findRoomsByHotel(Hotel hotel) {
        return rooms.stream()
                .filter(room -> room.getHotel().equals(hotel))
                .collect(Collectors.toList());
    }

    /**
     * Displays rooms for a specific hotel with pricing based on selected dates
     * @param hotel The hotel whose rooms should be displayed
     * @param resultsContainer The VBox container where room results will be displayed
     * @param checkInDate The selected check-in date
     * @param checkOutDate The selected check-out date
     */
    public void displayRoomsForHotel(Hotel hotel, VBox resultsContainer, LocalDate checkInDate, LocalDate checkOutDate) {
        // Clear previous results
        resultsContainer.getChildren().clear();

        // Add back button to return to hotel list
        Button backButton = new Button("Back to Hotels");
        backButton.setOnAction(e -> displayHotelsInCity(hotel.getCity(), resultsContainer, checkInDate, checkOutDate));

        // Add hotel header with name and star rating
        Label hotelHeader = new Label(hotel.getName() + " - " + hotel.getStars() + " stars");
        hotelHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Add back button and hotel header to the container
        resultsContainer.getChildren().addAll(backButton, hotelHeader);

        // Find all rooms for this hotel
        List<Room> hotelRooms = findRoomsByHotel(hotel);

        if (hotelRooms.isEmpty()) {
            // Display message when no rooms are available
            Label noRoomsLabel = new Label("No rooms available");
            resultsContainer.getChildren().add(noRoomsLabel);
        } else {
            // Calculate the total stay duration in days
            long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            if (days < 1) days = 1; // Ensure minimum stay is 1 day

            // Display each room with details, pricing, and booking option
            for (Room room : hotelRooms) {
                // Create a styled container for room information
                VBox roomBox = new VBox(5);
                roomBox.setPadding(new Insets(10));
                roomBox.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5;");

                // Create labels for room details
                Label typeLabel = new Label("Type: " + room.getType());
                Label descLabel = new Label("Description: " + room.getDescription());
                Label priceLabel = new Label("Price per day: $" + room.getPrice());

                // Calculate and display total price for the entire stay
                Label totalLabel = new Label("Total for " + days + " days: $" + (room.getPrice() * days));

                // Show how many rooms of this type are available
                Label availableLabel = new Label("Available: " + room.getAvailable());

                // Add all room details to the room container
                roomBox.getChildren().addAll(typeLabel, descLabel, priceLabel, totalLabel, availableLabel);

                // Create "Add to Cart" button
                Button addToCartButton = new Button("Add to Cart");

                // Set action for the "Add to Cart" button
                addToCartButton.setOnAction(e -> {
                    // Add room to cart
                    boolean added = bookingController.addToCart(room);

                    if (added) {
                        // Update UI to reflect new availability
                        bookingController.updateRoomUI(roomBox, room, addToCartButton, availableLabel);

                        // Show confirmation message
                        Label confirmLabel = new Label("Room added to cart!");
                        confirmLabel.setStyle("-fx-text-fill: green;");
                        roomBox.getChildren().add(confirmLabel);
                    }
                });

                // Add the button to the room box
                roomBox.getChildren().add(addToCartButton);

                // Update UI based on initial availability
                bookingController.updateRoomUI(roomBox, room, addToCartButton, availableLabel);

                // Add the room box to the results container
                resultsContainer.getChildren().add(roomBox);
            }
        }
    }
}
