module org.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.json;


    opens org.example.demo to javafx.fxml;
    exports org.example;
    opens org.example to javafx.fxml;
    exports org.example.Controller;
    opens org.example.Controller to javafx.fxml;
}