package ftp.client.FTPClient;

import ftp.client.FTPClient.file.FTPFile;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by alex on 2/16/2017.
 */
//TODO javaDoc
public interface FTPClient {
    void login(String userName, String password);
    void logout();
    List<FTPFile> getDirectoryFiles(String path);
    void renameFile(FTPFile file, String newName);
    void deleteFile(FTPFile file);
    void createFile(FTPFile file);
    void changeDirectory(String path);
    void abort();
    FTPFile getRootDirectoryName(String path);

    boolean testConnection(String host, int port, String userName, String userPassword);
    String sendCommand(String command);
    Logger getLogger();

    void setHost(String host);
    void setPort(int port);
}