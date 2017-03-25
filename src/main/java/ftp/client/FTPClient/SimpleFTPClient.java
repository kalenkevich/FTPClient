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

    @Override
    public boolean disconnect() {
        return logout();
    }

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

    @Override
    public CompletableFuture<List<FTPFile>> getDirectoryFilesAsync(String pathName) {
        return ftpConnection.listAsync(pathName);
    }

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

    @Override
    public CompletableFuture<Boolean> createFileAsync(File file, String path) {
        return ftpConnection.storAsync(file, path);
    }

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

    @Override
    public CompletableFuture<File> getFileAsync(FTPFile ftpFile, String localFilePath) {
        return ftpConnection.retrAsync(ftpFile.getPath(), localFilePath);
    }

    @Override
    public boolean changeDirectory(FTPFile ftpFile) {
        String fileName = ftpFile.getPath();
        boolean successResult = false;

        try {
            successResult = ftpConnection.cwd(fileName);
        } catch (IOException e) {
            logger.error(e);
        }

        return successResult;
    }

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

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
        ftpConnection.setLogger(logger);
    }

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