package ftp.client.FTPClient;

import ftp.client.FTPClient.connection.FTPConnection;
import ftp.client.FTPClient.connection.SimpleFTPConnection;
import ftp.client.FTPClient.file.FTPFile;
import ftp.client.FTPClient.report.FTPConnectionReport;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by alex on 2/8/2017.
 */
public class SimpleFTPClient implements FTPClient {
    private FTPConnection ftpConnection;
    private Logger logger;
    private String host;
    private int port;
    private boolean isConnected;
    private int timeToLive;

    public SimpleFTPClient() {
        initDefaultValues();
    }

    private void initDefaultValues() {
        logger = Logger.getLogger(SimpleFTPClient.class);
        ftpConnection = new SimpleFTPConnection();
        isConnected = false;
        timeToLive = 250;
    }


    /**
     * Connects to ftp server
     * @param host a server domain name
     * @param port an open server port
     * @return     a result of connection (success/fail)
     */
    @Override
    public boolean connect(String host, int port) {
        boolean successResult = false;
        this.host = host;
        this.port = port;

        try {
            successResult = ftpConnection.connect(host, port);
        } catch (IOException e) {
            logger.error(e);
        }

        return successResult;
    }

    /**
     * Disconnects from ftp server
     * @return     void
     */
    @Override
    public boolean disconnect() {
        return logout();
    }

    /**
     * Sends an user info to the server
     * @param userName a user name
     * @param password a user password
     * @return         a result of connection (success/fail)
     */
    @Override
    public boolean login(String userName, String password) {
        boolean successResult = false;

        try {
            successResult = ftpConnection.user(userName);
            if (successResult) {
                successResult = ftpConnection.pass(password);
            }
            isConnected = true;
        } catch (IOException e) {
            logger.error(e);
            isConnected = false;
        }

        return successResult;
    }

    /**
     * Logs out an user from the server session
     * @return         a result of operation (success/fail)
     */
    @Override
    public boolean logout() {
        boolean successResult = false;
        try {
            if (isConnected) {
                successResult = ftpConnection.disconnect();
                isConnected = false;
            }
        } catch (IOException e) {
            logger.error(e);
        }

        return successResult;
    }

    /**
     * Returns a list of ftp files
     * @param path a path ot a directory
     * @return     a list of ftp files
     */
    @Override
    public List<FTPFile> getDirectoryFiles(String path) {
        List<FTPFile> ftpFiles = new ArrayList<>();
        try {
            ftpFiles = ftpConnection.list(path);
        } catch (IOException e) {
            logger.error(e);
        }

        return ftpFiles;
    }

    /**
     * Returns a list of ftp files asynchronous
     * @param pathName a path ot a directory
     * @return         a list of ftp files
     */
    @Override
    public CompletableFuture<List<FTPFile>> getDirectoryFilesAsync(String pathName) {
        return ftpConnection.listAsync(pathName);
    }

    /**
     * Renames a file
     * @param file    a ftp (remote) file
     * @param newName a next name
     * @return        a result of operation (success/fail)
     */
    @Override
    public boolean renameFile(FTPFile file, String newName) {
        boolean successResult = false;
        String currentFileName = file.getPath();

        try {
            successResult = ftpConnection.rnfr(currentFileName);
            if (successResult) {
                successResult = ftpConnection.rnto(newName);
            }
        } catch (IOException e) {
            logger.error(e);
        }

        return successResult;
    }

    /**
     * Removes a file
     * @param file    a ftp (remote) file
     * @return        a result of operation (success/fail)
     */
    @Override
    public boolean deleteFile(FTPFile file) {
        boolean successResult = false;
        String filePath = file.getPath();

        try {
            successResult = ftpConnection.dele(filePath);
        } catch (IOException e) {
            logger.error(e);
        }

        return successResult;
    }

    /**
     * Creates a file
     * @param file    a ftp (remote) file
     * @param path    a path of directory
     * @return        a result of operation (success/fail)
     */
    @Override
    public boolean createFile(File file, String path) {
        boolean successResult = false;

        try {
            successResult = ftpConnection.stor(file, path);
        } catch (IOException e) {
            logger.error(e);
        }

        return successResult;
    }

    /**
     * Creates a file asynchronous
     * @param file    a ftp (remote) file
     * @param path    a path of directory
     * @return        a result of operation (success/fail) in the futures
     */
    @Override
    public CompletableFuture<Boolean> createFileAsync(File file, String path) {
        return ftpConnection.storAsync(file, path);
    }

    /**
     * Returns a file's modification time
     * @param ftpFile a ftp (remote) file
     * @return        a date
     */
    @Override
    public Date getFileModificationTime(FTPFile ftpFile) {
        Date modificationTime = null;

        try {
            modificationTime = ftpConnection.mdtm(ftpFile.getPath());
        } catch (IOException e) {
            logger.error(e);
        }

        return modificationTime;
    }

    /**
     * Returns a file's size
     * @param ftpFile a ftp (remote) file
     * @return        a size of a file
     */
    @Override
    public int getFileSize(FTPFile ftpFile) {
        int fileSize = -1;

        try {
            fileSize = ftpConnection.size(ftpFile.getPath());
        } catch (IOException e) {
            logger.error(e);
        }

        return fileSize;
    }

    /**
     * Returns a file
     * @param ftpFile       a ftp (remote) file
     * @param localFilePath a path to save a file (remote) file
     * @return              a file
     */
    @Override
    public File getFile(FTPFile ftpFile, String localFilePath) {
        String fileName = ftpFile.getPath();
        File file = null;

        try {
            file = ftpConnection.retr(fileName, localFilePath);
        } catch (IOException e) {
            logger.error(e);
        }

        return file;
    }

    /**
     * Returns a file asynchronous
     * @param ftpFile       a ftp (remote) file
     * @param localFilePath a path to save a file (remote) file
     * @return              a file in the future
     */
    @Override
    public CompletableFuture<File> getFileAsync(FTPFile ftpFile, String localFilePath) {
        return ftpConnection.retrAsync(ftpFile.getPath(), localFilePath);
    }

    /**
     * Changes a file directory
     * @param ftpFile a ftp (remote) file
     * @return        a result of operation (success/fail)
     */
    @Override
    public boolean changeDirectory(FTPFile ftpFile) { // where is a new directory's path?
        String fileName = ftpFile.getPath();
        boolean successResult = false;

        try {
            successResult = ftpConnection.cwd(fileName);
        } catch (IOException e) {
            logger.error(e);
        }

        return successResult;
    }

    /**
     * Aborts any operation
     * @return  a result of abortion (success/fail)
     */
    @Override
    public boolean abort() {
        boolean successResult = false;

        try {
            successResult = ftpConnection.abor();
        } catch (IOException e) {
            logger.error(e);
        }

        return successResult;
    }

    /**
     * Returns a root directory's path
     * @param path a pathname of a current directory
     * @return     a root directory name
     */
    @Override
    public String getRootDirectoryName(String path) {
        String directoryName = null;
        try {
            boolean success = ftpConnection.cwd(path);
            if (success) {
                success = ftpConnection.cdup();
                if (success) {
                    directoryName = ftpConnection.pwd();
                }
            }
        } catch (IOException e) {
            logger.error(e);
        }

        return directoryName;
    }

    /**
     * Tests the connection between a host and a ftp-server
     * @param host         a domain name
     * @param port         a server port
     * @param userName     a name of an user
     * @param userPassword a user's password
     * @return             a connection report object
     */
    @Override
    public FTPConnectionReport testConnection(String host, int port, String userName, String userPassword) {
        FTPConnection testFTPConnection = new SimpleFTPConnection();
        FTPConnectionReport connectionReport = new FTPConnectionReport();

        try {
            connectionReport.setSuccessConnection(testFTPConnection.connect(host, port));
        } catch (IOException e) {
            logger.error(e);

            return connectionReport;
        }

        try {
            testFTPConnection.user(userName);
            connectionReport.setSuccessUserLogin(testFTPConnection.pass(userPassword));
        } catch (IOException e) {
            logger.error(e);
        }

        try {
            connectionReport.setSuccessCommand(testFTPConnection.noop());
        } catch (IOException e) {
            logger.error(e);
        }

        try {
            connectionReport.setSuccessDisconnect(testFTPConnection.disconnect());
        } catch (IOException e) {
            logger.error(e);
        }

        return connectionReport;
    }

    /**
     * Sets a host
     * @param host a new host url
     * @return     void
     */
    @Override
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Sets a port
     * @param port a new server port
     * @return     void
     */
    @Override
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Returns a logger
     * @return a logger object
     */
    @Override
    public Logger getLogger() {
        return logger;
    }

    /**
     * Sets a next logger
     * @param logger a new logger object
     * @return       void
     */
    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
        ftpConnection.setLogger(logger);
    }

    /**
     * Recconects to the server
     * @return    a result of an operation (success/fail)
     */
    @Override
    public boolean reconnect() {
        boolean success = false;

        try {
            success = ftpConnection.rein();
        } catch (IOException e) {
            logger.error(e);
        }

        return success;
    }

    public FTPConnection getFtpConnection() {
        return ftpConnection;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}