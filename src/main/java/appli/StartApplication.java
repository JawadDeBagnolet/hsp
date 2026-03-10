package appli;

import appli.hsp.exception.ErrorCode;
import appli.hsp.exception.HSPException;
import appli.hsp.utils.ErrorHandler;
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
        mainStage = stage;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/appli/hsp/helloView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), mainStage.getMaxWidth(), mainStage.getMaxHeight());
            mainStage.setTitle("HSP - Lycée de Santé Publique");
            mainStage.setScene(scene);
            mainStage.show();
        } catch (Exception e) {
            ErrorHandler.handleException(
                new HSPException(ErrorCode.SYSTEM_ERROR, "Impossible de démarrer l'application", e),
                "Démarrage de l'application"
            );
            throw e;
        }
    }

    public static void main(String[] args) {
        launch();
    }

    public static void changeScene(String nomDuFichierFxml) throws IOException {
        String resourcePath = "/appli/hsp/" + nomDuFichierFxml + ".fxml";
        URL resourceUrl = StartApplication.class.getResource(resourcePath);
        System.out.println("[changeScene] Loading FXML: " + resourcePath + " -> " + resourceUrl);

        if (resourceUrl == null) {
            HSPException exception = ErrorHandler.createNavigationException(
                "La page demandée n'est pas disponible: " + nomDuFichierFxml, 
                resourcePath
            );
            ErrorHandler.handleException(exception, "Changement de scène vers " + nomDuFichierFxml, true);
            throw new IOException(exception.getMessage());
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(resourceUrl);
            Scene scene = new Scene(fxmlLoader.load(), mainStage.getWidth(), mainStage.getHeight());
            mainStage.setScene(scene);

            System.out.println("[changeScene] Scene set. root=" + scene.getRoot().getClass().getName());
            mainStage.setTitle("HSP - " + nomDuFichierFxml);
            mainStage.sizeToScene();
            mainStage.show();
            System.out.println("[changeScene] Stage showing=" + mainStage.isShowing() + ", title=" + mainStage.getTitle());
        } catch (Exception e) {
            HSPException exception = new HSPException(
                ErrorCode.SCENE_CHANGE_FAILED, 
                "Erreur lors du chargement de la page: " + nomDuFichierFxml, 
                e
            );
            ErrorHandler.handleException(exception, "Changement de scène vers " + nomDuFichierFxml, true);
            throw new IOException(exception.getMessage(), exception);
        }
    }

}