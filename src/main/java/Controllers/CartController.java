package Controllers;

import Models.Hotel;
import Models.Room;
import Models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
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

    /** User making the booking */
    private User user;

    /** Map to store individual check-in and check-out dates for each room */
    private Map<Room, LocalDate[]> roomDates = new java.util.HashMap<>();

    /**
     * Constructor to create a new CartController with required dependencies
     * @param bookingController The RoomBookingController to access cart data
     * @param checkInDate The check-in date for the booking
     * @param checkOutDate The check-out date for the booking
     * @param user The user making the booking
     */
    public CartController(RoomBookingController bookingController, LocalDate checkInDate, LocalDate checkOutDate, User user) {
        this.bookingController = bookingController;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.user = user;

        // Initialize room dates with individual dates for each room
        List<Room> cart = bookingController.getCart();
        if (cart != null && !cart.isEmpty()) {
            int i = 0;
            for (Room room : cart) {
                // Create slightly different dates for each room (for demonstration)
                LocalDate roomCheckIn = checkInDate.plusDays(i % 2);  // Vary check-in by 0 or 1 days
                LocalDate roomCheckOut = checkOutDate.plusDays(i % 3); // Vary check-out by 0, 1, or 2 days
                roomDates.put(room, new LocalDate[]{roomCheckIn, roomCheckOut});
                i++;
            }
        }
    }

    /**
     * Constructor to create a new CartController with required dependencies (without user)
     * @param bookingController The RoomBookingController to access cart data
     * @param checkInDate The check-in date for the booking
     * @param checkOutDate The check-out date for the booking
     */
    public CartController(RoomBookingController bookingController, LocalDate checkInDate, LocalDate checkOutDate) {
        this.bookingController = bookingController;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;

        // Initialize room dates with individual dates for each room
        List<Room> cart = bookingController.getCart();
        if (cart != null && !cart.isEmpty()) {
            int i = 0;
            for (Room room : cart) {
                // Create slightly different dates for each room (for demonstration)
                LocalDate roomCheckIn = checkInDate.plusDays(i % 2);  // Vary check-in by 0 or 1 days
                LocalDate roomCheckOut = checkOutDate.plusDays(i % 3); // Vary check-out by 0, 1, or 2 days
                roomDates.put(room, new LocalDate[]{roomCheckIn, roomCheckOut});
                i++;
            }
        }
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
        // Check if we have a user, if not, we can't proceed
        if (user == null) {
            System.out.println("Error: No user logged in");
            return;
        }

        // Create a new stage for collecting user details
        Stage userDetailsStage = new Stage();
        userDetailsStage.initModality(Modality.APPLICATION_MODAL);
        userDetailsStage.setTitle("Enter Your Details");

        // Create a grid pane for the form
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        // Add labels and text fields for real name, surname, and card number
        Label realNameLabel = new Label("Name:");
        TextField nameField = new TextField();

        Label surnameLabel = new Label("Surname:");
        TextField surnameField = new TextField();

        Label cardNumberLabel = new Label("Card Number:");
        TextField cardNumberField = new TextField();
        cardNumberField.setPromptText("XXXX-XXXX-XXXX-XXXX");

        // Add components to the grid
        grid.add(realNameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(surnameLabel, 0, 1);
        grid.add(surnameField, 1, 1);
        grid.add(cardNumberLabel, 0, 2);
        grid.add(cardNumberField, 1, 2);

        // Create submit button
        Button submitButton = new Button("Submit");
        submitButton.setPadding(new Insets(10, 20, 10, 20));

        // Add button to a horizontal box for centering
        HBox buttonBox = new HBox(submitButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        // Add the grid and button box to a vertical box
        VBox root = new VBox(20, grid, buttonBox);
        root.setPadding(new Insets(20));

        // Set action for the submit button
        submitButton.setOnAction(e -> {
            // Get the real name, surname, and card number
            String name = nameField.getText().trim();
            String surname = surnameField.getText().trim();
            String cardNumber = cardNumberField.getText().trim();

            // Validate input
            if (name.isEmpty() || surname.isEmpty() || cardNumber.isEmpty()) {
                // Show error message
                Label errorLabel = new Label("Please fill in all fields");
                errorLabel.setStyle("-fx-text-fill: red;");

                // Check if error label already exists
                if (root.getChildren().size() > 2) {
                    root.getChildren().set(2, errorLabel);
                } else {
                    root.getChildren().add(errorLabel);
                }
                return;
            }

            // Basic validation for card number format
            if (!cardNumber.matches("\\d{4}-\\d{4}-\\d{4}-\\d{4}") && !cardNumber.matches("\\d{16}")) {
                // Show error message
                Label errorLabel = new Label("Please enter a valid card number (XXXX-XXXX-XXXX-XXXX)");
                errorLabel.setStyle("-fx-text-fill: red;");

                // Check if error label already exists
                if (root.getChildren().size() > 2) {
                    root.getChildren().set(2, errorLabel);
                } else {
                    root.getChildren().add(errorLabel);
                }
                return;
            }

            // Set the real name and surname in the user object
            user.setRealName(name);
            user.setSurname(surname);

            // We acknowledge the card number was entered correctly
            // In a real application, we would process the payment here
            System.out.println("Card number validated: " + cardNumber);

            // Close the user details stage
            userDetailsStage.close();

            // Show payment confirmation
            showPaymentConfirmation(cartStage);
        });

        // Create the scene with the root container
        Scene scene = new Scene(root, 350, 200);

        // Set the scene and show the stage
        userDetailsStage.setScene(scene);
        userDetailsStage.show();
    }

    /**
     * Shows the payment confirmation dialog
     * @param cartStage The cart stage to close after successful payment
     */
    private void showPaymentConfirmation(Stage cartStage) {
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

            // Write user information if available
            if (user != null) {
                writer.write("CUSTOMER INFORMATION:\n");
                writer.write("===============================\n");
                if (user.getRealName() != null && user.getSurname() != null) {
                    writer.write("Name: " + user.getRealName() + " " + user.getSurname() + "\n");
                }
                writer.write("Email: " + user.getEmail() + "\n\n");
            }

            // Write booking date
            writer.write("BOOKING INFORMATION:\n");
            writer.write("===============================\n");
            writer.write("Booking Date: " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "\n");

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

                // Use room-specific dates if available, otherwise use the general dates
                LocalDate roomCheckIn = checkInDate;
                LocalDate roomCheckOut = checkOutDate;
                if (roomDates.containsKey(room)) {
                    LocalDate[] dates = roomDates.get(room);
                    roomCheckIn = dates[0];
                    roomCheckOut = dates[1];
                }

                writer.write("Check-in Date: " + roomCheckIn.format(DateTimeFormatter.ISO_LOCAL_DATE) + "\n");
                writer.write("Check-out Date: " + roomCheckOut.format(DateTimeFormatter.ISO_LOCAL_DATE) + "\n");

                // Calculate price for this room using room-specific dates
                double roomPrice = bookingController.calculateTotalPrice(room, roomCheckIn, roomCheckOut);
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

        // Get the username from the user object if available, otherwise use "user"
        String userName = (user != null) ? user.getNickName() : "user";

        // Create file name
        return dateStr + "_" + userName + "_" + randomID + ".txt";
    }
}
