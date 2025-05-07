module com.example.final_oop {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.final_oop to javafx.fxml;
    exports com.example.final_oop;
    opens Models to javafx.fxml;
    exports Models;
    opens App to javafx.fxml;
    exports App;
}
