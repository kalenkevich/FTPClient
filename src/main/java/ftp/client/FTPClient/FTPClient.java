package ftp.client.FTPClient;

import ftp.client.FTPClient.connection.connector.FTPConnectionException;
import ftp.client.FTPClient.file.FTPFile;
import ftp.client.FTPClient.report.FTPConnectionReport;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by alex on 2/16/2017.
 * FTPClient is the interface for FTPClient
 */
public interface FTPClient {

    /**
     * Connects to ftp server
     * @param host a server domain name
     * @param port an open server port
     */
    void connect(String host, int port) throws FTPConnectionException;

    /**
     * Disconnects from ftp server
     */
    void disconnect() throws FTPConnectionException;

    /**
     * Sends an user info to the server
     * @param userName a user name
     * @param password a user password
     */
    void login(String userName, String password) throws FTPConnectionException;

    /**
     * Logs out an user from the server session
     */
    void logout() throws FTPConnectionException;

    /**
     * Recconects to the server
     */
    void reconnect() throws FTPConnectionException;
    /**
     * Returns a list of ftp files
     * @param path a path ot a directory
     * @return     a list of ftp files
     */
    List<FTPFile> getDirectoryFiles(String path) throws FTPConnectionException;

    /**
     * Returns a list of ftp files asynchronous
     * @param pathName a path ot a directory
     * @return         a list of ftp files
     */
    CompletableFuture<List<FTPFile>> getDirectoryFilesAsync(String pathName) throws FTPConnectionException;

    /**
     * Renames a file
     * @param file    a ftp (remote) file
     * @param newName a next name
     */
    void renameFile(FTPFile file, String newName) throws FTPConnectionException;

    /**
     * Removes a file
     * @param file    a ftp (remote) file
     */
    void deleteFile(FTPFile file) throws FTPConnectionException;

    /**
     * Creates a file
     * @param file    a ftp (remote) file
     * @param path    a path of directory
     */
    void createFile(File file, String path) throws FTPConnectionException, FileNotFoundException;

    /**
     * Creates a file asynchronous
     * @param file    a ftp (remote) file
     * @param path    a path of directory
     * @return        a result of operation (success/fail) in the futures
     */
    CompletableFuture createFileAsync(File file, String path) throws FTPConnectionException;

    /**
     * Returns a file's modification time
     * @param ftpFile a ftp (remote) file
     * @return        a date
     */
    Date getFileModificationTime(FTPFile ftpFile) throws FTPConnectionException;

    /**
     * Returns a file's size
     * @param ftpFile a ftp (remote) file
     * @return        a size of a file
     */
    int getFileSize(FTPFile ftpFile) throws FTPConnectionException;

    /**
     * Returns a file
     * @param ftpFile       a ftp (remote) file
     * @param localFilePath a path to save a file (remote) file
     * @return              a file
     */
    File getFile(FTPFile ftpFile, String localFilePath) throws IOException;

    /**
     * Returns a file asynchronous
     * @param ftpFile       a ftp (remote) file
     * @param localFilePath a path to save a file (remote) file
     * @return              a file in the future
     */
    CompletableFuture<File> getFileAsync(FTPFile ftpFile, String localFilePath) throws FTPConnectionException;

    /**
     * Changes a file directory
     * @param ftpFile a ftp (remote) file
     */
    void changeDirectory(FTPFile ftpFile) throws FTPConnectionException;

    /**
     * Aborts any operation
     */
    void abort() throws FTPConnectionException;

    /**
     * Returns a root directory's path
     * @param path a pathname of a current directory
     * @return     a root directory name
     */
    String getRootDirectoryName(String path) throws FTPConnectionException;

    /**
     * Tests the connection between a host and a ftp-server
     * @param host         a domain name
     * @param port         a server port
     * @param userName     a name of an user
     * @param userPassword a user's password
     * @return             a connection report object
     */
    FTPConnectionReport testConnection(String host, int port, String userName, String userPassword) throws FTPConnectionException;

    /**
     * Returns a logger
     * @return a logger object
     */
    Logger getLogger();

    /**
     * Sets a next logger
     * @param logger a new logger object
     */
    void setLogger(Logger logger);

    /**
     * Sets a host
     * @param host a new host url
     */
    void setHost(String host);

    /**
     * Sets a port
     * @param port a new server port
     */
    void setPort(int port);
}