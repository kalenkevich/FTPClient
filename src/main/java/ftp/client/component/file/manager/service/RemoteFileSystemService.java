package ftp.client.component.file.manager.service;

import ftp.client.FTPClient.FTPClient;
import ftp.client.component.file.FileItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a.kalenkevich on 08.02.2017.
 */
public class RemoteFileSystemService implements FileSystemService {
    private FTPClient ftpClient;

    public RemoteFileSystemService(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    @Override
    public List<FileItem> getFilesFromDirectory(String directoryName) {
        return new ArrayList<>();
    }

    public FTPClient getFtpConnection() {
        return ftpClient;
    }

    public void setFtpConnection(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}