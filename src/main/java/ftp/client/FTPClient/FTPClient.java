package ftp.client.FTPClient;

import ftp.client.FTPClient.file.FTPFile;
import ftp.client.FTPClient.report.FTPConnectionReport;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by alex on 2/16/2017.
 * FTPClient is the interface for FTPClient
 */
public interface FTPClient {
    boolean connect(String host, int port);
    boolean disconnect();
    boolean login(String userName, String password);
    boolean logout();
    boolean reconnect();
    List<FTPFile> getDirectoryFiles(String path);
    CompletableFuture<List<FTPFile>> getDirectoryFilesAsync(String pathName);
    boolean renameFile(FTPFile file, String newName);
    boolean deleteFile(FTPFile file);
    boolean createFile(File file, String path);
    CompletableFuture<Boolean> createFileAsync(File file, String path);
    Date getFileModificationTime(FTPFile ftpFile);
    int getFileSize(FTPFile ftpFile);
    File getFile(FTPFile ftpFile, String localFilePath);
    CompletableFuture<File> getFileAsync(FTPFile ftpFile, String localFilePath);
    boolean changeDirectory(FTPFile path);
    boolean abort();
    String getRootDirectoryName(String path);
    FTPConnectionReport testConnection(String host, int port, String userName, String userPassword);
    Logger getLogger();
    void setLogger(Logger logger);
    void setHost(String host);
    void setPort(int port);
}