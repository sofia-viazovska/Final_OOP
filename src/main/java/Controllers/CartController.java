package Controllers;

import Models.Hotel;
import Models.Room;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Controller class responsible for cart functionality.
 * Manages the display of cart contents, payment processing, and booking confirmation.
 */
public class CartController {
    /** Reference to the RoomBookingController to access cart data */
    private RoomBookingController bookingController;

    /** Check-in date for the booking */
    private LocalDate checkInDate;

    /** Check-out date for the booking */
    private LocalDate checkOutDate;

    /**
     * Constructor to create a new CartController with required dependencies
     * @param bookingController The RoomBookingController to access cart data
     * @param checkInDate The check-in date for the booking
     * @param checkOutDate The check-out date for the booking
     */
    public CartController(RoomBookingController bookingController, LocalDate checkInDate, LocalDate checkOutDate) {
        this.bookingController = bookingController;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    /**
     * Displays the cart contents in a new window
     */
    public void displayCart() {
        // Create a new stage for the cart view
        Stage cartStage = new Stage();
        cartStage.initModality(Modality.APPLICATION_MODAL);
        cartStage.setTitle("Your Cart");

        // Create a container for cart items
        VBox cartContainer = new VBox(10);
        cartContainer.setPadding(new Insets(20));

        // Get cart contents
        List<Room> cart = bookingController.getCart();

        if (cart.isEmpty()) {
            // Display message when cart is empty
            Label emptyCartLabel = new Label("Your cart is empty");
            cartContainer.getChildren().add(emptyCartLabel);
        } else {
            // Add header
            Label headerLabel = new Label("Your Booked Rooms");
            headerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            cartContainer.getChildren().add(headerLabel);

            // Track total price
            double totalPrice = 0;

            // Display each room in the cart
            for (Room room : cart) {
                // Create a container for room information
                HBox roomBox = new HBox(10);
                roomBox.setPadding(new Insets(10));
                roomBox.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5;");

                // Create a vertical box for room details
                VBox roomInfo = new VBox(5);

                // Get hotel information
                Hotel hotel = room.getHotel();

                // Create labels for room and hotel details
                Label hotelLabel = new Label(hotel.getName() + " - " + hotel.getCity());
                hotelLabel.setStyle("-fx-font-weight: bold;");

                Label roomTypeLabel = new Label("Room: " + room.getType());
                Label roomDescLabel = new Label("Description: " + room.getDescription());

                // Calculate price for this room
                double roomPrice = bookingController.calculateTotalPrice(room, checkInDate, checkOutDate);
                totalPrice += roomPrice;

                Label priceLabel = new Label(String.format("Price: $%.2f", roomPrice));

                // Add all room details to the room info container
                roomInfo.getChildren().addAll(hotelLabel, roomTypeLabel, roomDescLabel, priceLabel);

                // Add room info to the room box
                roomBox.getChildren().add(roomInfo);

                // Add the room box to the cart container
                cartContainer.getChildren().add(roomBox);
            }

            // Add separator
            Label separatorLabel = new Label("----------------------------------------");
            cartContainer.getChildren().add(separatorLabel);

            // Add total price
            Label totalLabel = new Label(String.format("Total Price: $%.2f", totalPrice));
            totalLabel.setStyle("-fx-font-weight: bold;");
            cartContainer.getChildren().add(totalLabel);

            // Add "Pay and Book" button
            Button payButton = new Button("Pay and Book");
            payButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            payButton.setPadding(new Insets(10, 20, 10, 20));

            // Set action for the "Pay and Book" button
            payButton.setOnAction(e -> {
                // Process payment and generate booking file
                processPayment(cartStage);
            });

            // Add button to container
            HBox buttonBox = new HBox(payButton);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(20, 0, 0, 0));
            cartContainer.getChildren().add(buttonBox);
        }

        // Create a scroll pane for the cart container
        ScrollPane scrollPane = new ScrollPane(cartContainer);
        scrollPane.setFitToWidth(true);

        // Create the scene with the scroll pane
        Scene scene = new Scene(scrollPane, 500, 600);

        // Set the scene and show the stage
        cartStage.setScene(scene);
        cartStage.show();
    }

    /**
     * Processes the payment and generates a booking confirmation file
     * @param cartStage The stage to close after successful payment
     */
    private void processPayment(Stage cartStage) {
        // Create a new stage for the payment confirmation
        Stage confirmStage = new Stage();
        confirmStage.initModality(Modality.APPLICATION_MODAL);
        confirmStage.setTitle("Payment Confirmation");

        // Create a container for the confirmation message
        VBox confirmContainer = new VBox(20);
        confirmContainer.setPadding(new Insets(30));
        confirmContainer.setAlignment(Pos.CENTER);

        // Create confirmation message
        Label confirmLabel = new Label("Successfully paid");
        confirmLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: green;");

        // Create OK button
        Button okButton = new Button("OK");
        okButton.setPadding(new Insets(10, 20, 10, 20));

        // Set action for the OK button
        okButton.setOnAction(e -> {
            // Generate booking file
            generateBookingFile();

            // Clear the cart
            bookingController.clearCart();

            // Close both stages
            confirmStage.close();
            cartStage.close();
        });

        // Add message and button to the container
        confirmContainer.getChildren().addAll(confirmLabel, okButton);

        // Create the scene with the container
        Scene scene = new Scene(confirmContainer, 300, 200);

        // Set the scene and show the stage
        confirmStage.setScene(scene);
        confirmStage.show();
    }

    /**
     * Generates a booking confirmation file with all booking details
     */
    private void generateBookingFile() {
        try {
            // Get cart contents
            List<Room> cart = bookingController.getCart();

            // Generate file name
            String fileName = generateFileName();

            // Create file
            File bookingFile = new File(fileName);
            FileWriter writer = new FileWriter(bookingFile);

            // Write booking date
            writer.write("Booking Date: " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "\n\n");

            // Write check-in and check-out dates
            writer.write("Check-in Date: " + checkInDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "\n");
            writer.write("Check-out Date: " + checkOutDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "\n\n");

            // Write booking details for each room
            writer.write("BOOKING DETAILS:\n");
            writer.write("===============================\n\n");

            double totalPrice = 0;

            for (Room room : cart) {
                Hotel hotel = room.getHotel();

                writer.write("Hotel: " + hotel.getName() + "\n");
                writer.write("City: " + hotel.getCity() + "\n");
                writer.write("Room Type: " + room.getType() + "\n");
                writer.write("Description: " + room.getDescription() + "\n");

                // Calculate price for this room
                double roomPrice = bookingController.calculateTotalPrice(room, checkInDate, checkOutDate);
                totalPrice += roomPrice;

                writer.write(String.format("Price: $%.2f\n\n", roomPrice));
            }

            // Write total price
            writer.write("===============================\n");
            writer.write(String.format("TOTAL PRICE: $%.2f\n", totalPrice));

            // Close the writer
            writer.close();

            System.out.println("Booking file created: " + fileName);
        } catch (IOException e) {
            System.err.println("Error creating booking file: " + e.getMessage());
        }
    }

    /**
     * Generates a file name in the format "yearmonthday_username_randomID.txt"
     * @return The generated file name
     */
    private String generateFileName() {
        // Get current date
        LocalDate today = LocalDate.now();
        String dateStr = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Generate random 5-digit ID
        Random random = new Random();
        int randomID = 10000 + random.nextInt(90000); // 5-digit number between 10000 and 99999

        // Use "user" as the username since we don't have a user object
        String userName = "user";

        // Create file name
        return dateStr + "_" + userName + "_" + randomID + ".txt";
    }
}
