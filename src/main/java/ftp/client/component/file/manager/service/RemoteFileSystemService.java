package ftp.client.component.file.manager.service;

import ftp.client.FTPClient.FTPClient;
import ftp.client.component.file.FileItem;

import java.io.File;
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
        File file = null;
        try {
            file = ftpClient.getFtpConnection().list(directoryName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @Override
    public void renameFile(File file, String name) {

    }

    @Override
    public void deleteFile(File file) {

    }

    @Override
    public void addFile(File file) {

    }

    public FTPClient getFtpConnection() {
        return ftpClient;
    }

    public void setFtpConnection(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}