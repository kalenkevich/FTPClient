package ftp.client.FTPClient;

import ftp.client.FTPClient.file.FTPFile;
import ftp.client.FTPClient.report.FTPConnectionReport;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * Created by alex on 2/16/2017.
 */
//TODO javaDoc
public interface FTPClient {
    boolean connect(String host, int port);
    boolean disconnect();
    boolean login(String userName, String password);
    boolean logout();
    List<FTPFile> getDirectoryFiles(String path);
    boolean renameFile(FTPFile file, String newName);
    boolean deleteFile(FTPFile file);
    boolean createFile(File file);
    File getFile(FTPFile file);
    boolean changeDirectory(String path);
    boolean abort();
    FTPFile getRootDirectoryName(String path);

    FTPConnectionReport testConnection(String host, int port, String userName, String userPassword);
    String sendCommand(String command);
    Logger getLogger();

    void setHost(String host);
    void setPort(int port);
}