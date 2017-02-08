package sample.window.manager;

import javafx.scene.control.TextArea;
import sample.FTPClient.FTPConnection;
import sample.component.file.manager.FileManagerController;

/**
 * Created by a.kalenkevich on 08.02.2017.
 */
public class WindowManager implements FileManagerEventListener {

    private FileManagerController localFileManager;
    private FileManagerController remoteFileManager;
    private FTPConnection ftpConnection;
    private boolean isActive;

    public WindowManager(FileManagerController localFileManager, FileManagerController remoteFileManager, FTPConnection ftpConnection, TextArea loggerTextArea) {
        this.localFileManager = localFileManager;
        this.remoteFileManager = remoteFileManager;
        this.ftpConnection = ftpConnection;
        this.ftpConnection.getLogger().addAppender(new TextFieldLoggerAppender(loggerTextArea));
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

    public FTPConnection getFtpConnection() {
        return ftpConnection;
    }

    public void setFtpConnection(FTPConnection ftpConnection) {
        this.ftpConnection = ftpConnection;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public void handleEvent() {

    }
}