module com.example.hsp {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens appli.hsp to javafx.fxml;
    opens appli to javafx.graphics;
    exports appli.hsp;
    exports appli;
}
