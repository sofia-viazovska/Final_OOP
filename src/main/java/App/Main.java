package App;

import Controllers.CartController;
import Controllers.HotelFindController;
import Controllers.RoomBookingController;
import Models.Hotel;
import Models.Room;
import Models.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;

/**
 * Main application class for the Hotel Booking System.
 * This class serves as the entry point for the application and is responsible for:
 * 1. Initializing sample data (hotels and rooms)
 * 2. Setting up the user interface
 * 3. Handling user interactions
 * 4. Displaying search results
 */
public class Main extends Application {

    // Controllers for handling hotel search and room booking functionality
    private RoomBookingController roomBookingController;
    private HotelFindController hotelFindController;

    // Currently logged in user
    private User currentUser;

    @Override
    public void start(Stage primaryStage) {
        // Show login UI first
        showLoginUI(primaryStage);
    }

    /**
     * Shows the login UI to authenticate the user
     * @param primaryStage The primary stage for the application
     */
    private void showLoginUI(Stage primaryStage) {
        // Create a new stage for the login form
        primaryStage.setTitle("Login - Booking System");

        // Create a grid pane for the login form
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Create a scene with the grid pane
        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);

        // Add a welcome label
        Label welcomeLabel = new Label("Welcome to Booking System");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        grid.add(welcomeLabel, 0, 0, 2, 1);

        // Add labels and text fields for nickname, password, and email
       //Label nicknameLabel = new Label("Nickname:");
        //grid.add(nicknameLabel, 0, 1);

        //TextField nicknameField = new TextField();
        //grid.add(nicknameField, 1, 1);

        Label emailLabel = new Label("Email:");
        grid.add(emailLabel, 0, 3);

        TextField emailField = new TextField();
        grid.add(emailField, 1, 3);

        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 2);

        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 2);

        // Add login button
        Button loginButton = new Button("Login");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(loginButton);
        grid.add(hbBtn, 1, 4);

        // Add message label for errors or success
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");
        grid.add(messageLabel, 0, 5, 2, 1);

        // Set action for the login button
        loginButton.setOnAction(e -> {
            // Get the input values
            String password = passwordField.getText().trim();
            String email = emailField.getText().trim();

            // Validate input
            if (password.isEmpty() || email.isEmpty()) {
                messageLabel.setText("Please fill in all fields");
                return;
            }

            // Validate email format (basic check)
            if (!email.contains("@") || !email.contains(".")) {
                messageLabel.setText("Please enter a valid email address");
                return;
            }

            // Create user object
            currentUser = new User(email, password);

            // Show loading screen and initialize data
            showLoadingAndInitialize(primaryStage);
        });

        // Show the login form
        primaryStage.show();
    }

    /**
     * Shows loading screen and initializes data
     * @param primaryStage The primary stage for the application
     */
    private void showLoadingAndInitialize(Stage primaryStage) {
        // Create a loading indicator
        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(100, 100);

        // Create a label for the loading message
        Label loadingLabel = new Label("Initializing data...");
        loadingLabel.setStyle("-fx-font-size: 14px;");

        // Create a vertical box for the loading indicator and message
        VBox loadingBox = new VBox(20, loadingIndicator, loadingLabel);
        loadingBox.setAlignment(Pos.CENTER);

        // Create a scene with the loading indicator
        Scene loadingScene = new Scene(loadingBox, 300, 200);

        // Set the loading scene and show the stage
        primaryStage.setScene(loadingScene);
        primaryStage.setTitle("Booking System");
        primaryStage.show();

        // Create a task for asynchronous data initialization
        Task<Void> initDataTask = new Task<>() {
            @Override
            protected Void call() {
                initializeSampleData();
                return null;
            }
        };

        // Handle task completion
        initDataTask.setOnSucceeded(event -> {
            // Create controllers for handling hotel search and room booking functionality
            roomBookingController = new RoomBookingController();
            hotelFindController = new HotelFindController(roomBookingController);

            // Set up the UI on the JavaFX Application Thread
            Platform.runLater(() -> {
                setupUI(primaryStage);
            });
        });

        // Start the task in a new thread
        new Thread(initDataTask).start();
    }

    /**
     * Initializes sample data for hotels and rooms
     */
    private void initializeSampleData() {
        /* ===== SAMPLE DATA INITIALIZATION ===== */
        // Create and add sample hotels organized by city

        // New York hotels
        Hotel grandHotel = new Hotel("Grand Hotel", "+1234567890", 5, "New York", "Luxury hotel in downtown");
        Hotel comfortInn = new Hotel("Comfort Inn", "+0987654321", 3, "New York", "Affordable comfort");
        Hotel plazaHotel = new Hotel("Plaza Hotel", "+2223334444", 5, "New York", "Historic luxury hotel");
        Hotel broadwayMotel = new Hotel("Broadway Motel", "+5556667777", 2, "New York", "Budget-friendly near theaters");

        HotelFindController.addHotel(grandHotel);
        HotelFindController.addHotel(comfortInn);
        HotelFindController.addHotel(plazaHotel);
        HotelFindController.addHotel(broadwayMotel);

        // Miami hotels
        Hotel beachResort = new Hotel("Beach Resort", "+1122334455", 4, "Miami", "Beautiful beachfront property");
        Hotel oceanView = new Hotel("Ocean View", "+9988776655", 5, "Miami", "Luxury oceanfront resort");
        Hotel palmSuites = new Hotel("Palm Suites", "+1231231234", 3, "Miami", "Family-friendly hotel with pool");

        HotelFindController.addHotel(beachResort);
        HotelFindController.addHotel(oceanView);
        HotelFindController.addHotel(palmSuites);

        // Denver hotels
        Hotel mountainLodge = new Hotel("Mountain Lodge", "+5566778899", 3, "Denver", "Scenic mountain views");
        Hotel alpineResort = new Hotel("Alpine Resort", "+4445556666", 4, "Denver", "Ski-in/ski-out luxury resort");

        HotelFindController.addHotel(mountainLodge);
        HotelFindController.addHotel(alpineResort);

        // Los Angeles hotels
        Hotel hollywoodStar = new Hotel("Hollywood Star", "+7778889999", 4, "Los Angeles", "Close to Hollywood attractions");
        Hotel beverlyHillsHotel = new Hotel("Beverly Hills Hotel", "+3334445555", 5, "Los Angeles", "Exclusive luxury experience");
        Hotel sunsetMotel = new Hotel("Sunset Motel", "+6667778888", 2, "Los Angeles", "Affordable option on Sunset Blvd");

        HotelFindController.addHotel(hollywoodStar);
        HotelFindController.addHotel(beverlyHillsHotel);
        HotelFindController.addHotel(sunsetMotel);

        // Chicago hotels
        Hotel windyCityInn = new Hotel("Windy City Inn", "+8889990000", 3, "Chicago", "Comfortable downtown hotel");
        Hotel lakesideHotel = new Hotel("Lakeside Hotel", "+1112223333", 4, "Chicago", "Beautiful views of Lake Michigan");

        HotelFindController.addHotel(windyCityInn);
        HotelFindController.addHotel(lakesideHotel);

        // Boston hotels
        Hotel historicInn = new Hotel("Historic Inn", "+4443332222", 4, "Boston", "Charming hotel in historic district");
        Hotel universityLodge = new Hotel("University Lodge", "+7776665555", 3, "Boston", "Convenient for campus visits");

        HotelFindController.addHotel(historicInn);
        HotelFindController.addHotel(universityLodge);

        // Add sample rooms
        // New York hotel rooms
        HotelFindController.addRoom(new Room(grandHotel, "Standard", 150, 5, "Comfortable room with queen bed"));
        HotelFindController.addRoom(new Room(grandHotel, "Deluxe", 250, 3, "Spacious room with king bed and city view"));
        HotelFindController.addRoom(new Room(grandHotel, "Suite", 400, 2, "Luxury suite with separate living area"));

        HotelFindController.addRoom(new Room(comfortInn, "Standard", 80, 8, "Basic room with double bed"));
        HotelFindController.addRoom(new Room(comfortInn, "Double", 120, 5, "Room with two double beds"));

        HotelFindController.addRoom(new Room(plazaHotel, "Classic", 200, 10, "Elegant room with queen bed"));
        HotelFindController.addRoom(new Room(plazaHotel, "Executive", 350, 5, "Luxury room with king bed and park view"));
        HotelFindController.addRoom(new Room(plazaHotel, "Presidential Suite", 800, 1, "Opulent suite with butler service"));

        HotelFindController.addRoom(new Room(broadwayMotel, "Basic", 60, 12, "Simple room with double bed"));
        HotelFindController.addRoom(new Room(broadwayMotel, "Family", 90, 6, "Room with two queen beds"));

        // Miami hotel rooms
        HotelFindController.addRoom(new Room(beachResort, "Ocean View", 180, 8, "Room with balcony and ocean view"));
        HotelFindController.addRoom(new Room(beachResort, "Pool View", 150, 10, "Room overlooking the pool area"));
        HotelFindController.addRoom(new Room(beachResort, "Beach Suite", 300, 4, "Suite with direct beach access"));

        HotelFindController.addRoom(new Room(oceanView, "Deluxe Ocean", 250, 15, "Deluxe room with panoramic ocean view"));
        HotelFindController.addRoom(new Room(oceanView, "Premium Suite", 450, 5, "Premium suite with private balcony"));

        HotelFindController.addRoom(new Room(palmSuites, "Standard", 100, 20, "Comfortable room for families"));
        HotelFindController.addRoom(new Room(palmSuites, "Cabana", 150, 8, "Room with direct pool access"));

        // Add rooms for other hotels
        HotelFindController.addRoom(new Room(mountainLodge, "Mountain View", 120, 10, "Room with scenic mountain views"));
        HotelFindController.addRoom(new Room(alpineResort, "Ski Suite", 220, 5, "Suite with ski-in/ski-out access"));
        HotelFindController.addRoom(new Room(hollywoodStar, "Celebrity Suite", 300, 3, "Suite with Hollywood memorabilia"));
        HotelFindController.addRoom(new Room(beverlyHillsHotel, "Luxury Room", 400, 8, "Opulent room with premium amenities"));
        HotelFindController.addRoom(new Room(sunsetMotel, "Standard", 70, 15, "Basic clean room for budget travelers"));
        HotelFindController.addRoom(new Room(windyCityInn, "City View", 110, 12, "Room with Chicago skyline view"));
        HotelFindController.addRoom(new Room(lakesideHotel, "Lake View", 160, 8, "Room with beautiful lake views"));
        HotelFindController.addRoom(new Room(historicInn, "Historic Suite", 180, 5, "Suite in the historic wing"));
        HotelFindController.addRoom(new Room(universityLodge, "Scholar Room", 90, 20, "Comfortable room near campus"));
    }

    /**
     * Sets up the user interface
     * @param primaryStage The primary stage for the application
     */
    private void setupUI(Stage primaryStage) {
        /* ===== USER INTERFACE SETUP ===== */
        // Create and configure UI components for the booking form

        // Top bar with nickname, cart button, and logout button
        Label nicknameLabel = new Label("User: " + currentUser.getNickName());
        nicknameLabel.setPadding(new Insets(10));
        nicknameLabel.setStyle("-fx-font-weight: bold;");

        Button cartButton = new Button("Cart");
        cartButton.setPadding(new Insets(5));
        cartButton.setAlignment(Pos.TOP_RIGHT);
        cartButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        // Cart button event handler will be set after all UI components are defined

        Button logoutButton = new Button("Log out");
        logoutButton.setPadding(new Insets(5));
        logoutButton.setOnAction(e -> logout(primaryStage));

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(10);
        topBar.getChildren().addAll(nicknameLabel);

        // Push cart and logout buttons to the right
        HBox.setHgrow(nicknameLabel, javafx.scene.layout.Priority.ALWAYS);
        topBar.getChildren().addAll(logoutButton, cartButton);

        // Application title
        Label nameLabel = new Label("Booking System");
        nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        nameLabel.setPadding(new Insets(20));
        nameLabel.setAlignment(Pos.CENTER);
        HBox nameBox = new HBox(nameLabel);
        nameBox.setAlignment(Pos.CENTER);

        // Check-in date input section
        Label checkInDate = new Label("Check-in Date:");
        //checkInDate.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        checkInDate.setPadding(new Insets(10));
        checkInDate.setAlignment(Pos.BASELINE_RIGHT);
        HBox checkInDateBox = new HBox(checkInDate);

        DatePicker datePickerIn = new DatePicker();
        checkInDateBox.getChildren().add(datePickerIn);

        // Check-out date input section
        Label checkOutDate = new Label("Check-out Date:");
        //checkInDate.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        checkOutDate.setPadding(new Insets(10));
        checkOutDate.setAlignment(Pos.BASELINE_RIGHT);
        HBox checkOutDateBox = new HBox(checkOutDate);

        DatePicker datePickerOut = new DatePicker();
        checkInDateBox.getChildren().add(datePickerOut);
        checkOutDateBox.getChildren().add(datePickerOut);

        // Combine date pickers into one horizontal container
        HBox Dates = new HBox(checkInDateBox, checkOutDateBox);

        // City input section
        Label cityLabel = new Label("City:");
        cityLabel.setPadding(new Insets(10));
        cityLabel.setAlignment(Pos.BASELINE_RIGHT);
        HBox cityBox = new HBox(cityLabel);

        TextField cityField = new TextField();
        cityField.setPromptText("Enter city name");
        cityField.setPadding(new Insets(10));
        cityBox.getChildren().add(cityField);

        // Search button
        Button findButton = new Button("Find");
        findButton.setPadding(new Insets(10));

        // Add button to a horizontal box
        HBox findButtonBox = new HBox(10, findButton);
        findButtonBox.setAlignment(Pos.CENTER);

        // Container for displaying search results
        VBox resultsContainer = new VBox();
        resultsContainer.setPadding(new Insets(10));
        resultsContainer.setSpacing(5);

        // Wrap results in a scroll pane for scrolling
        ScrollPane resultsScrollPane = new ScrollPane(resultsContainer);
        resultsScrollPane.setFitToWidth(true);
        resultsScrollPane.setPrefHeight(300);

        // Error message label
        Label errorMessageLabel = new Label();
        errorMessageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        errorMessageLabel.setPadding(new Insets(5, 0, 5, 0));

        // Wrap the error message label in an HBox for centering
        HBox errorMessageBox = new HBox(errorMessageLabel);
        errorMessageBox.setAlignment(Pos.CENTER);
        errorMessageBox.setVisible(false);

        // Controllers are already created in the start method and stored as instance variables

        /* ===== EVENT HANDLERS ===== */
        // Define actions for user interactions

        // Cart button click handler
        cartButton.setOnAction(event -> {
            // Validate that date pickers have values
            if (datePickerIn.getValue() == null || datePickerOut.getValue() == null) {
                errorMessageLabel.setText("Please select dates before viewing cart.");
                errorMessageBox.setVisible(true);
                return;
            }

            // Validate check-in date is not before today
            if (datePickerIn.getValue().isBefore(LocalDate.now())) {
                errorMessageLabel.setText("Check-in date cannot be before today.");
                errorMessageBox.setVisible(true);
                return;
            }

            // Validate check-out date is after check-in date
            if (datePickerOut.getValue().isBefore(datePickerIn.getValue()) ||
                datePickerOut.getValue().isEqual(datePickerIn.getValue())) {
                errorMessageLabel.setText("Check-out date must be after check-in date.");
                errorMessageBox.setVisible(true);
                return;
            }

            // Clear any previous error messages
            errorMessageBox.setVisible(false);

            // Create CartController and display cart
            CartController cartController = new CartController(
                roomBookingController,
                datePickerIn.getValue(),
                datePickerOut.getValue(),
                currentUser
            );
            cartController.displayCart();
        });

        // Search button click handler
        findButton.setOnMouseClicked(event -> {
            // Get the city name from the text field
            String city = cityField.getText();

            // Validate that date pickers have values
            if (datePickerIn.getValue() == null || datePickerOut.getValue() == null) {
                errorMessageLabel.setText("Please select dates.");
                errorMessageBox.setVisible(true);
                return;
            }

            // Validate check-in date is not before today
            if (datePickerIn.getValue().isBefore(LocalDate.now())) {
                errorMessageLabel.setText("Check-in date cannot be before today.");
                errorMessageBox.setVisible(true);
                return;
            }

            // Validate check-out date is after check-in date
            if (datePickerOut.getValue().isBefore(datePickerIn.getValue()) ||
                datePickerOut.getValue().isEqual(datePickerIn.getValue())) {
                errorMessageLabel.setText("Check-out date must be after check-in date.");
                errorMessageBox.setVisible(true);
                return;
            }

            // Convert date values to strings for validation
            String checkIn = datePickerIn.getValue().toString();
            String checkOut = datePickerOut.getValue().toString();

            // Validate that all required fields are filled
            if (city.isEmpty() || checkIn.isEmpty() || checkOut.isEmpty()) {
                errorMessageLabel.setText("Please fill in all fields.");
                errorMessageBox.setVisible(true);
            } else {
                // Clear any previous error messages
                errorMessageBox.setVisible(false);

                // Log search parameters and perform the search
                System.out.println("Searching for hotels in " + city + " from " + checkIn + " to " + checkOut);

                // Use HotelFindController to find and display hotels matching the criteria
                hotelFindController.displayHotelsInCity(city, resultsContainer, datePickerIn.getValue(), datePickerOut.getValue());
            }
        });

        /* ===== FINAL UI ASSEMBLY ===== */
        // Arrange all UI components in the main layout
        VBox root = new VBox(topBar, nameBox, Dates, cityBox, findButtonBox, errorMessageBox, resultsScrollPane);

        // Create the scene with the root layout and set dimensions
        Scene scene = new Scene(root, 650, 600);

        // Configure and display the primary stage (main window)
        primaryStage.setScene(scene);
        primaryStage.setTitle("Booking System");
        primaryStage.show();
    }

    /**
     * Handles user logout
     * @param primaryStage The primary stage for the application
     */
    private void logout(Stage primaryStage) {
        // Reset the current user
        currentUser = null;

        // Return to the login screen
        showLoginUI(primaryStage);
    }

    /**
     * Main method that launches the JavaFX application
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
