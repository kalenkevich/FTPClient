package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.FTPClient.FTPConnection;
import sample.service.RouterService;
import sample.user.User;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static sample.config.Consts.*;

public class MainController implements Initializable, Controller {

    @FXML
    private TabPane tabPane;

    public void initialize(URL location, ResourceBundle resources) {
        User user = showAuthorisationPopup();
        setupNewConnection(user);
    }

    @FXML
    public void newSessionAction(ActionEvent actionEvent) {

    }

    private void setupNewConnection(User user) {
        FTPConnection ftpConnection = new FTPConnection();
        try {
            ftpConnection.connect(DEFAULT_FTP_SERVER_URL, DEFAULT_COMMAND_PORT, user.getName(),user.getPassword());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private User showAuthorisationPopup() {
        Pane view  = (Pane) RouterService.getInstance().getView(POPUP_VIEW);
        Stage stage = RouterService.getInstance().getPrimaryStage();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Login");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(stage);
        Scene scene = new Scene(view);
        dialogStage.setScene(scene);

        AuthorisationController controller = (AuthorisationController) RouterService.getInstance().getController();
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();

        return controller.getUser();
    }
}