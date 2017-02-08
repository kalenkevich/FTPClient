package sample.window.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import sample.FTPClient.FTPConnection;
import sample.window.manager.WindowManager;
import sample.component.file.manager.FileManagerController;
import sample.service.RouterService;
import sample.FTPClient.user.User;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import static sample.config.Consts.*;

public class MainController implements Initializable, Controller {

    @FXML
    private TabPane tabPane;
    private List<WindowManager> windowManagers;
    private Logger logger = Logger.getLogger(MainController.class);

    public void initialize(URL location, ResourceBundle resources) {
        setDefaults();
        newSessionAction();
    }

    private void setDefaults() {
        windowManagers = new ArrayList<>();
    }

    @FXML
    public void newSessionAction() {
        User user = showAuthorisationPopup();
        FTPConnection ftpConnection = setupNewConnection(user);

        if (ftpConnection != null) {
            createNewTab(ftpConnection, user);
        }
    }

    private FTPConnection setupNewConnection(User user) {
        FTPConnection ftpConnection = new FTPConnection();
        try {
            ftpConnection.connect(DEFAULT_FTP_SERVER_URL, DEFAULT_COMMAND_PORT, user.getName(),user.getPassword());
        } catch (IOException e) {
            logger.error(e);
        }
        return ftpConnection;
    }

    private void createNewTab(FTPConnection ftpConnection, User user) {
        Tab tab = new Tab();
        tab.setText(user.getName());
        SplitPane splitPane = new SplitPane();
        TableView localGrid = (TableView) RouterService.getInstance().getView(FILE_MANAGER_VIEW);
        FileManagerController localFileManagerController = (FileManagerController) RouterService.getInstance().getController();
        TableView remoteGrid = (TableView) RouterService.getInstance().getView(FILE_MANAGER_VIEW);
        FileManagerController remoteFileManagerController = (FileManagerController) RouterService.getInstance().getController();
        TextArea loggerTextArea = new TextArea();
        windowManagers.add(new WindowManager(localFileManagerController, remoteFileManagerController, ftpConnection, loggerTextArea));
        splitPane.getItems().add(localGrid);
        splitPane.getItems().add(remoteGrid);
        VBox vBox = new VBox();
        vBox.getChildren().add(splitPane);
        vBox.getChildren().add(loggerTextArea);
        tab.setContent(new AnchorPane(vBox));
        tabPane.getTabs().add(tab);
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