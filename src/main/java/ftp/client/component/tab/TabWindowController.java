package ftp.client.component.tab;

import ftp.client.FTPClient.FTPClient;
import ftp.client.FTPClient.SimpleFTPClient;
import ftp.client.logger.TextFieldLoggerAppender;
import ftp.client.service.AnchorService;
import ftp.client.user.User;
import ftp.client.component.file.manager.TableEventListener;
import ftp.client.component.file.manager.FileManagerController;
import ftp.client.component.file.service.LocalFileSystemService;
import ftp.client.component.file.service.RemoteFileSystemService;
import ftp.client.component.popup.authorisation.AuthorisationPopupController;
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

import java.util.ArrayList;
import java.util.List;

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
    private List<Object> items;
    private FileManagerController pastedController;

    @Override
    public void init() {
        ftpClient = new SimpleFTPClient();
        items = new ArrayList<>();
        insertElements();
        setupNewFTPClient();
        setDefaults();
    }

    private void insertElements() {
        Pane localGrid = (Pane) RouterService.getInstance().getView(Consts.FILE_MANAGER_VIEW);
        localFileManager = (FileManagerController) RouterService.getInstance().getController();
        localFileManager.init();
        Pane remoteGrid = (Pane) RouterService.getInstance().getView(Consts.FILE_MANAGER_VIEW);
        remoteFileManager = (FileManagerController) RouterService.getInstance().getController();
        remoteFileManager.init();
        AnchorService.getInstance().anchorNode(localGrid);
        AnchorService.getInstance().anchorNode(remoteGrid);
        splitPane.getItems().add(new AnchorPane(localGrid));
        splitPane.getItems().add(new AnchorPane(remoteGrid));

        Pane textBufferPane = (Pane) RouterService.getInstance().getView(Consts.TEXT_BUFFER_VIEW);
        TextBufferController textBufferController = (TextBufferController) RouterService.getInstance().getController();
        textBufferController.init();
        loggerTextArea = textBufferController.getTextArea();
        AnchorService.getInstance().anchorNode(textBufferPane);
        ((VBox) rootPane.getChildren().get(0)).getChildren().add(textBufferPane);

        Logger logger = Logger.getLogger(FTPClient.class);
        logger.addAppender(new TextFieldLoggerAppender(loggerTextArea));
        ftpClient.setLogger(logger);
    }

    private void setDefaults() {
        localFileManager.setFileSystemService(new LocalFileSystemService());
        remoteFileManager.setFileSystemService(new RemoteFileSystemService(ftpClient));
        localFileManager.addDragAndDropListener(this);
        remoteFileManager.addDragAndDropListener(this);
        localFileManager.setDirectoryPath("/");
        remoteFileManager.setDirectoryPath("/");
    }

    private void setupNewFTPClient() {
        User user = showAuthorisationPopup();
        name = user.getName();

        ftpClient.setHost(user.getHostName());
        ftpClient.setPort(user.getPort());
        ftpClient.connect(user.getHostName(), user.getPort());
        ftpClient.login(user.getName(), user.getPassword());
    }

    private User showAuthorisationPopup() {
        Pane view  = (Pane) RouterService.getInstance().getView(Consts.AUTHORISATION_POPUP_VIEW);
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

    private void notifyOtherFileManagersAboutCopyAction(List<Object> copiedItems, FileManagerController fileManagerController) {
        items = copiedItems;
        FileManagerController managerToNotify = fileManagerController == localFileManager ? remoteFileManager : localFileManager;
        pastedController = fileManagerController == localFileManager ? localFileManager : remoteFileManager;
        managerToNotify.updatePasteButtonDisabledState(false);
    }

    @Override
    public void onDrag(Object draggedItem, FileManagerController fileManagerController) {

    }

    @Override
    public void onDragExited(Object droppedItem, FileManagerController fileManagerController) {

    }

    @Override
    public void onDragEntered(Object droppedItem, FileManagerController fileManagerController) {

    }

    @Override
    public void onSelect(List<Object> droppedItems, FileManagerController fileManagerController) {

    }

    @Override
    public void onCut(List<Object> cutOutItems, FileManagerController fileManagerController) {

    }

    @Override
    public void onPaste(List<Object> pastedItems, FileManagerController fileManagerController) {

    }

    @Override
    public void onCopy(List<Object> copiedItems, FileManagerController fileManagerController) {
        notifyOtherFileManagersAboutCopyAction(copiedItems, fileManagerController);
    }

    @Override
    public List<Object> getItemsToCopy() {
        return items;
    }

    @Override
    public FileManagerController getCopyFileManagerController() {
        return pastedController;
    }
}