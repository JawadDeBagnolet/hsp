module com.example.hsp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens appli.example.hsp to javafx.fxml;
    exports appli.example.hsp;
}