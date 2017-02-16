package ftp.client.component.tab;

import ftp.client.FTPClient.FTPClient;
import ftp.client.user.User;
import ftp.client.component.command.line.CommandLineController;
import ftp.client.component.file.manager.TableEventListener;
import ftp.client.component.file.manager.FileManagerController;
import ftp.client.component.file.service.LocalFileSystemService;
import ftp.client.component.file.service.RemoteFileSystemService;
import ftp.client.component.popup.AuthorisationPopupController;
import ftp.client.component.text.buffer.TextBufferController;
import ftp.client.config.Consts;
import ftp.client.controller.Controller;
import ftp.client.service.RouterService;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by alex on 2/8/2017.
 */

//todo rename component
public class TabWindowController implements Controller, TableEventListener {
    @FXML
    private Pane rootPane;
    @FXML
    private SplitPane splitPane;
    private TextArea loggerTextArea;
    private FileManagerController localFileManager;
    private FileManagerController remoteFileManager;
    private boolean isActive;
    private FTPClient ftpClient;
    private static Logger logger = Logger.getLogger(TabWindowController.class);
    private String name;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ftpClient = new FTPClient();
        insertElements();
        setupNewFTPClient();
        setDefaults();
    }

    private void insertElements() {
        TableView localGrid = (TableView) RouterService.getInstance().getView(Consts.FILE_MANAGER_VIEW);
        localFileManager = (FileManagerController) RouterService.getInstance().getController();
        TableView remoteGrid = (TableView) RouterService.getInstance().getView(Consts.FILE_MANAGER_VIEW);
        remoteFileManager = (FileManagerController) RouterService.getInstance().getController();
        splitPane.getItems().add(new AnchorPane(localGrid));
        splitPane.getItems().add(new AnchorPane(remoteGrid));
        Pane commandLinePane = (Pane) RouterService.getInstance().getView(Consts.COMMAND_LINE_VIEW);
        CommandLineController commandLineController = (CommandLineController) RouterService.getInstance().getController();
        commandLineController.setFtpClient(ftpClient);
        Pane textBufferPane = (Pane) RouterService.getInstance().getView(Consts.TEXT_BUFFER_VIEW);
        TextBufferController textBufferController = (TextBufferController) RouterService.getInstance().getController();
        loggerTextArea = textBufferController.getTextArea();
        ((VBox) rootPane.getChildren().get(0)).getChildren().add(commandLinePane);
        ((VBox) rootPane.getChildren().get(0)).getChildren().add(textBufferPane);
    }

    private void setDefaults() {
        localFileManager.setFileSystemService(new LocalFileSystemService());
        remoteFileManager.setFileSystemService(new RemoteFileSystemService(ftpClient));
        localFileManager.addDragAndDropListener(this);
        remoteFileManager.addDragAndDropListener(this);
        localFileManager.setDirectory("/");
        remoteFileManager.setDirectory("/");
    }

    private void setupNewFTPClient() {
        User user = showAuthorisationPopup();
        name = user.getName();

        ftpClient.setHost(user.getHostName());
        ftpClient.setPort(user.getPort());

        ftpClient.getLogger().addAppender(new TextFieldLoggerAppender(loggerTextArea));
        ftpClient.login(user.getName(), user.getPassword());
    }

    private User showAuthorisationPopup() {
        Pane view  = (Pane) RouterService.getInstance().getView(Consts.POPUP_VIEW);
        AuthorisationPopupController authorisationPopupController = (AuthorisationPopupController) RouterService.getInstance().getController();
        Stage stage = RouterService.getInstance().getPrimaryStage();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Login");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(stage);
        Scene scene = new Scene(view);
        dialogStage.setScene(scene);
        authorisationPopupController.setDialogStage(dialogStage);
        dialogStage.showAndWait();

        return authorisationPopupController.getUser();
    }

    public FileManagerController getLocalFileManager() {
        return localFileManager;
    }

    public void setLocalFileManager(FileManagerController localFileManager) {
        this.localFileManager = localFileManager;
    }

    public FileManagerController getRemoteFileManager() {
        return remoteFileManager;
    }

    public void setRemoteFileManager(FileManagerController remoteFileManager) {
        this.remoteFileManager = remoteFileManager;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void onDrag(Object draggedItem, FileManagerController fileManagerController) {
        System.out.println("dragged " + draggedItem + " from " + fileManagerController);
    }

    @Override
    public void onDragExited(Object droppedItem, FileManagerController fileManagerController) {
        System.out.println("DragExited " + droppedItem + " from " + fileManagerController);
    }

    @Override
    public void onDragEntered(Object droppedItem, FileManagerController fileManagerController) {
        System.out.println("DragEntered " + droppedItem + " from " + fileManagerController);
    }

    @Override
    public void onSelect(List<Object> droppedItem, FileManagerController fileManagerController) {

    }
}