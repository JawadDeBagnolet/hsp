module com.example.hsp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.hsp to javafx.fxml;
    exports com.example.hsp;
}