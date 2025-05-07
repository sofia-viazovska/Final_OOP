package Controllers;

import Models.Room;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class responsible for room booking functionality.
 * Manages the cart system for booking rooms and updating room availability.
 */
public class RoomBookingController {
    /** Static list to store all rooms in the cart */
    private static List<Room> cart = new ArrayList<>();

    /** Map to store the quantity of each room type in the cart */
    private static Map<Room, Integer> cartQuantities = new HashMap<>();

    /**
     * Adds a room to the cart and decreases its availability
     * @param room The room to be added to the cart
     * @return true if the room was successfully added, false if it's not available
     */
    public boolean addToCart(Room room) {
        // Check if the room is available
        if (room.getAvailable() <= 0) {
            return false;
        }

        // Add room to cart
        cart.add(room);

        // Update cart quantities
        if (cartQuantities.containsKey(room)) {
            cartQuantities.put(room, cartQuantities.get(room) + 1);
        } else {
            cartQuantities.put(room, 1);
        }

        // Decrease room availability
        room.setAvailable(room.getAvailable() - 1);

        return true;
    }

    /**
     * Gets all rooms currently in the cart
     * @return List of rooms in the cart
     */
    public List<Room> getCart() {
        return cart;
    }

    /**
     * Gets the quantity of a specific room in the cart
     * @param room The room to check
     * @return The quantity of the room in the cart
     */
    public int getCartQuantity(Room room) {
        return cartQuantities.getOrDefault(room, 0);
    }

    /**
     * Clears the cart and restores room availability
     */
    public void clearCart() {
        // Restore room availability
        for (Map.Entry<Room, Integer> entry : cartQuantities.entrySet()) {
            Room room = entry.getKey();
            int quantity = entry.getValue();
            room.setAvailable(room.getAvailable() + quantity);
        }

        // Clear cart and quantities
        cart.clear();
        cartQuantities.clear();
    }

    /**
     * Updates the UI for a room box based on room availability
     * @param roomBox The VBox container for the room
     * @param room The room to update UI for
     * @param addToCartButton The "Add to Cart" button
     * @param availableLabel The label showing availability
     */
    public void updateRoomUI(VBox roomBox, Room room, Button addToCartButton, Label availableLabel) {
        // Update availability label
        availableLabel.setText("Available: " + room.getAvailable());

        // Check if room is available
        if (room.getAvailable() <= 0) {
            // Hide "Add to Cart" button
            addToCartButton.setVisible(false);
            addToCartButton.setManaged(false);

            // Show "No such room available" label
            Label noRoomLabel = new Label("No such room available");
            noRoomLabel.setStyle("-fx-text-fill: red;");
            roomBox.getChildren().add(noRoomLabel);
        } else {
            // Show "Add to Cart" button
            addToCartButton.setVisible(true);
            addToCartButton.setManaged(true);
        }
    }

    /**
     * Calculates the total price for a room based on the selected dates
     * @param room The room to calculate price for
     * @param checkInDate The check-in date
     * @param checkOutDate The check-out date
     * @return The total price for the stay
     */
    public double calculateTotalPrice(Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        // Calculate number of days
        long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (days < 1) days = 1; // Minimum 1 day

        // Calculate total price
        return room.getPrice() * days;
    }
}
