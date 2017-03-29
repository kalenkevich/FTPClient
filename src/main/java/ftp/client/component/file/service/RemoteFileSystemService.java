package ftp.client.component.file.service;

import ftp.client.FTPClient.FTPClient;
import ftp.client.FTPClient.SimpleFTPClient;
import ftp.client.FTPClient.connection.connector.FTPConnectionException;
import ftp.client.FTPClient.file.FTPFile;
import ftp.client.component.file.FileItem;
import ftp.client.component.file.RemoteFileItem;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by a.kalenkevich on 08.02.2017.
 */
public class RemoteFileSystemService implements FileSystemService {
    private FTPClient ftpClient;
    private Logger logger = Logger.getLogger(RemoteFileSystemService.class);
    private String currentDirectory;

    public RemoteFileSystemService(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    @Override
    public List<FileItem> getFilesFromDirectory(String directoryName) throws FileException {
        currentDirectory = directoryName;
        List<FTPFile> ftpFiles;
        try {
            ftpFiles = ftpClient.getDirectoryFiles(directoryName);
        } catch (FTPConnectionException e) {
            throw new FileException(e.getMessage());
        }

        return getFileItems(ftpFiles, directoryName);
    }

    //TODO MEGA DZICH TRY TO AVOID IT.
    @Override
    public CompletableFuture<List<FileItem>> getFilesFromDirectoryAsync(String directoryName) throws FileException {
        try {
            return ftpClient.getDirectoryFilesAsync(directoryName).thenApply((
                    ftpFiles -> {
                        try {
                            return getFileItems(ftpFiles, directoryName);
                        } catch (FileException e) {
                            try {
                                throw new FileException(e.getMessage());
                            } catch (FileException e1) {
                                e1.printStackTrace();
                            }
                        }

                        return null;
                    }
            ));
        } catch (FTPConnectionException e) {
            throw new FileException(e.getMessage());
        }
    }

    private List<FileItem> getFileItems(List<FTPFile> ftpFiles, String directoryName) throws FileException {
        List<FileItem> fileItems = new ArrayList<>();
        for (FTPFile ftpFile: ftpFiles) {
            fileItems.add(new RemoteFileItem(ftpFile));
        }

        FileItem rootFile = getRootFileItem(directoryName);
        if (rootFile == null) {
            String rootDirectoryName;
            try {
                rootDirectoryName = ftpClient.getRootDirectoryName(directoryName);
            } catch (FTPConnectionException e) {
                throw new FileException(e.getMessage());
            }
            FTPFile ftpFile = new FTPFile(rootDirectoryName);
            rootFile = new RemoteFileItem(ftpFile);
        }
        fileItems.add(0, rootFile);

        return fileItems;
    }

    @Override
    public void renameFile(FileItem file, String name) throws FileException {
        try {
            FTPFile ftpFile = getFTPFile(file);
            ftpClient.renameFile(ftpFile, name);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void deleteFile(FileItem file) throws FileException {
        try {
            FTPFile ftpFile = getFTPFile(file);
            ftpClient.deleteFile(ftpFile);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void addFile(File file) throws FileException {
        try {
            ftpClient.createFile(file, currentDirectory);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public CompletableFuture addFileAsync(File file) throws FileException {
        try {
            return ftpClient.createFileAsync(file, currentDirectory);
        } catch (FTPConnectionException e) {
            throw new FileException(e.getMessage());
        }
    }

    @Override
    public FileItem getRootFileItem(String path) throws FileException {
        FTPFile ftpFile;
        try {
            ftpFile = new FTPFile(ftpClient.getRootDirectoryName(path));
        } catch (FTPConnectionException e) {
            throw new FileException(e.getMessage());
        }
        ftpFile.setDirectory(true);

        return new RemoteFileItem(ftpFile);
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

    public File getFile(FileItem fileItem, String localFilePath) throws FileException {
        FTPFile ftpFile;
        File file;
        try {
            ftpFile = getFTPFile(fileItem);
            file = ftpClient.getFile(ftpFile, localFilePath);
            ftpFile.setFile(file);
        } catch (Exception e) {
            throw new FileException(e.getMessage());
        }

        return file;
    }

    @Override
    public CompletableFuture<File> getFileAsync(FileItem fileItem, String localFilePath) throws FileException {
        FTPFile ftpFile = null;
        try {
            ftpFile = getFTPFile(fileItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return ftpClient.getFileAsync(ftpFile, localFilePath);
        } catch (FTPConnectionException e) {
            throw new FileException(e.getMessage());
        }
    }
}