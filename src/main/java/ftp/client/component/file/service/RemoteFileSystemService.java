package ftp.client.component.file.service;

import ftp.client.FTPClient.FTPClient;
import ftp.client.component.file.FileItem;

import java.io.IOException;
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
        try {
             ftpClient.getFtpConnection().list(directoryName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @Override
    public void renameFile(FileItem file, String name) {

    }

    @Override
    public void deleteFile(FileItem file) {

    }

    @Override
    public void addFile(FileItem file) {

    }

    @Override
    public FileItem getRootFileItem(String path) {
        return null;
    }

    public FTPClient getFtpConnection() {
        return ftpClient;
    }

    public void setFtpConnection(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}