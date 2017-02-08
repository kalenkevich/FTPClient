package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.service.RouterService;

import static sample.config.Consts.MAIN_VIEW;
import static sample.config.Consts.WINDOW_HEIGHT;
import static sample.config.Consts.WINDOW_WIDTH;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        RouterService.getInstance().setPrimaryStage(primaryStage);
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(MAIN_VIEW));
        primaryStage.setTitle("FTPClient");
        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}