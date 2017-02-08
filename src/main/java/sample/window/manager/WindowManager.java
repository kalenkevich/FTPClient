package sample.window.manager;

import javafx.scene.control.TextArea;
import sample.FTPClient.FTPConnection;
import sample.component.file.manager.FileManagerController;
import sample.component.file.service.LocalFileSystemService;
import sample.component.file.service.RemoteFileSystemService;

/**
 * Created by a.kalenkevich on 08.02.2017.
 */
public class WindowManager implements FileManagerEventListener {

    private FileManagerController localFileManager;
    private FileManagerController remoteFileManager;
    private boolean isActive;

    public WindowManager(FileManagerController localFileManager, FileManagerController remoteFileManager, FTPConnection ftpConnection, TextArea loggerTextArea) {
        this.localFileManager = localFileManager;
        this.remoteFileManager = remoteFileManager;
        localFileManager.setFileSystemService(new LocalFileSystemService());
        remoteFileManager.setFileSystemService(new RemoteFileSystemService(ftpConnection));
        ftpConnection.getLogger().addAppender(new TextFieldLoggerAppender(loggerTextArea));

        initialize();
    }

    private void initialize() {
        localFileManager.setDirectory("/");
        remoteFileManager.setDirectory("/");
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

    @Override
    public void handleEvent() {

    }
}