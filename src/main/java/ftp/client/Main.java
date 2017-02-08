package ftp.client;

import ftp.client.config.Consts;
import ftp.client.service.RouterService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        RouterService.getInstance().setPrimaryStage(primaryStage);
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(Consts.MAIN_VIEW));
        primaryStage.setTitle("FTPClient");
        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(Consts.WINDOW_WIDTH);
        primaryStage.setHeight(Consts.WINDOW_HEIGHT);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}