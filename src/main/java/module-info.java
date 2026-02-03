module com.example.hsp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens appli.example.hsp to javafx.fxml;
    opens appli to javafx.graphics;
    exports appli.hsp;
    exports appli;
}
