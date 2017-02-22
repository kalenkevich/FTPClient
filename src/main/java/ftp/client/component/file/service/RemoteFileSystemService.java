package ftp.client.component.file.service;

import ftp.client.FTPClient.FTPClient;
import ftp.client.FTPClient.SimpleFTPClient;
import ftp.client.FTPClient.file.FTPFile;
import ftp.client.component.file.FileItem;
import ftp.client.component.file.RemoteFileItem;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by a.kalenkevich on 08.02.2017.
 */
public class RemoteFileSystemService implements FileSystemService {
    private FTPClient ftpClient;
    private Logger logger = Logger.getLogger(RemoteFileSystemService.class);

    public RemoteFileSystemService(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    @Override
    public List<FileItem> getFilesFromDirectory(String directoryName) {
        List<FTPFile> ftpFiles = ftpClient.getDirectoryFiles(directoryName);
        List<FileItem> fileItems = new ArrayList<>();
        for (FTPFile ftpFile: ftpFiles) {
            fileItems.add(new RemoteFileItem(ftpFile));
        }

        FileItem rootFile = getRootFileItem(directoryName);
        if (rootFile == null) {
            FTPFile ftpFile = new FTPFile("/");
            rootFile = new RemoteFileItem(ftpFile);
        }
        fileItems.add(0, rootFile);

        return fileItems;
    }

    @Override
    public void renameFile(FileItem file, String name) {
        try {
            FTPFile ftpFile = getFTPFile(file);
            ftpClient.renameFile(ftpFile, name);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void deleteFile(FileItem file) {
        try {
            FTPFile ftpFile = getFTPFile(file);
            ftpClient.deleteFile(ftpFile);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void addFile(FileItem file) {
        try {
            FTPFile ftpFile = getFTPFile(file);
            ftpClient.createFile(ftpFile.getFile());
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public FileItem getRootFileItem(String path) {
        FTPFile ftpFile = ftpClient.getRootDirectoryName(path);
        if (ftpFile != null) {
            new RemoteFileItem(ftpFile);
        }

        return null;
    }

    public FTPClient getFtpConnection() {
        return ftpClient;
    }

    public void setFtpConnection(SimpleFTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    private FTPFile getFTPFile(FileItem fileItem) throws Exception {
        if (fileItem instanceof RemoteFileItem) {
            return ((RemoteFileItem)fileItem).getFTPFile();
        }

        throw new Exception("Not Remote file instance of file");
    }
}