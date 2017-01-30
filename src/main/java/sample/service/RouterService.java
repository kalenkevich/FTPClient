package sample.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import sample.controller.Controller;

import java.io.IOException;

/**
 * Created by alex on 1/30/2017.
 */
public class RouterService {
    private static RouterService ourInstance = new RouterService();

    public static RouterService getInstance() {
        return ourInstance;
    }

    private RouterService() {

    }

    private Stage stage;

    public void setPrimaryStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getPrimaryStage() {
        return stage;
    }

    private FXMLLoader loader;

    public Controller getController() {
        return loader.getController();
    }

    public Parent getView(String path) {
        if (loader == null) {
            loader = new FXMLLoader();
        }
        loader.setLocation(getClass().getClassLoader().getResource(path));
        Parent view = null;
        try {
            view = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }
}
