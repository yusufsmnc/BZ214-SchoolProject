package bm.erciyes.robotvacuumsim;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Launcher.class.getResource("main-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(
                Launcher.class.getResource("styles.css").toExternalForm()
        );
        stage.setTitle("Robot Süpürge Simülasyonu");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}