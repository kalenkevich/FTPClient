package sample.component.file.service;

import sample.FTPClient.FTPConnection;
import sample.component.file.FileItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a.kalenkevich on 08.02.2017.
 */
public class RemoteFileSystemService implements FileSystemService {
    private FTPConnection ftpConnection;

    public RemoteFileSystemService(FTPConnection ftpConnection) {
        this.ftpConnection = ftpConnection;
    }

    @Override
    public List<FileItem> getFilesFromDirectory(String directoryName) {
        return new ArrayList<>();
    }

    public FTPConnection getFtpConnection() {
        return ftpConnection;
    }

    public void setFtpConnection(FTPConnection ftpConnection) {
        this.ftpConnection = ftpConnection;
    }
}