package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.service.RouterService;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        RouterService.getInstance().setPrimaryStage(primaryStage);
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("view/sample.fxml"));
        primaryStage.setTitle("FTPClient");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
