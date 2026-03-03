package appli;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class StartApplication extends Application {
    private static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        mainStage=stage;
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/appli/hsp/helloView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), mainStage.getMaxWidth(), mainStage.getMaxHeight());
        mainStage.setTitle("Hello!");
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void changeScene(String nomDuFichierFxml ) throws IOException {
        String resourcePath = "/appli/hsp/" + nomDuFichierFxml + ".fxml";
        URL resourceUrl = StartApplication.class.getResource(resourcePath);
        System.out.println("[changeScene] Loading FXML: " + resourcePath + " -> " + resourceUrl);

        if (resourceUrl == null) {
            throw new IOException("FXML introuvable: " + resourcePath);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(resourceUrl);
        Scene scene = new Scene(fxmlLoader.load(), mainStage.getWidth(), mainStage.getHeight());
        mainStage.setScene(scene);

        System.out.println("[changeScene] Scene set. root=" + scene.getRoot().getClass().getName());
        mainStage.setTitle("HSP - " + nomDuFichierFxml);
        mainStage.sizeToScene();
        mainStage.show();
        System.out.println("[changeScene] Stage showing=" + mainStage.isShowing() + ", title=" + mainStage.getTitle());
    }

}