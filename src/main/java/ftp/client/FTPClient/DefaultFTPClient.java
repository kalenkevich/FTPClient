package ftp.client.FTPClient;

import ftp.client.FTPClient.connection.FTPConnection;
import ftp.client.FTPClient.connection.DefaultFTPConnection;
import ftp.client.FTPClient.connection.connector.FTPConnectionException;
import ftp.client.FTPClient.connection.connector.DefaultFTPConnector;
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
 * Created by alex on 2/8/2017.
 */
public class DefaultFTPClient implements FTPClient {
    private FTPConnection ftpConnection;
    private Logger logger;
    private String host;
    private int port;
    private boolean isConnected;

    public DefaultFTPClient(FTPConnection ftpConnection) {
        initDefaultValues();
        this.ftpConnection = ftpConnection;
    }

    private void initDefaultValues() {
        logger = Logger.getLogger(DefaultFTPClient.class);
        isConnected = false;
    }

    @Override
    public void connect(String host, int port) throws FTPConnectionException {
        this.host = host;
        this.port = port;

        ftpConnection.connect(host, port);
    }

    @Override
    public void disconnect() throws FTPConnectionException {
        logout();
    }

    @Override
    public void login(String userName, String password) throws FTPConnectionException {
        boolean successResult = ftpConnection.user(userName);

        if (successResult) {
            ftpConnection.pass(password);
            isConnected = true;
        }
    }

    @Override
    public void logout() throws FTPConnectionException {
        if (isConnected) {
            ftpConnection.disconnect();
            isConnected = false;
        }
    }

    @Override
    public List<FTPFile> getDirectoryFiles(String path) throws FTPConnectionException {
        return ftpConnection.list(path);
    }

    @Override
    public CompletableFuture<List<FTPFile>> getDirectoryFilesAsync(String pathName) throws FTPConnectionException {
        return ftpConnection.listAsync(pathName);
    }

    @Override
    public void renameFile(FTPFile file, String newName) throws FTPConnectionException {
        String currentFileName = file.getPath();
        boolean successResult = ftpConnection.rnfr(currentFileName);

        if (successResult) {
            ftpConnection.rnto(newName);
        }
    }

    @Override
    public void deleteFile(FTPFile file) throws FTPConnectionException {
        ftpConnection.dele(file.getPath());
    }

    @Override
    public void createFile(File file, String path) throws FTPConnectionException, FileNotFoundException {
        ftpConnection.stor(file, path);
    }

    @Override
    public CompletableFuture<Boolean> createFileAsync(File file, String path) throws FTPConnectionException {
        return ftpConnection.storAsync(file, path);
    }

    @Override
    public Date getFileModificationTime(FTPFile ftpFile) throws FTPConnectionException {
        return ftpConnection.mdtm(ftpFile.getPath());
    }

    @Override
    public int getFileSize(FTPFile ftpFile) throws FTPConnectionException {
        return ftpConnection.size(ftpFile.getPath());
    }

    @Override
    public File getFile(FTPFile ftpFile, String localFilePath) throws IOException {
        return ftpConnection.retr(ftpFile.getPath(), localFilePath);
    }

    @Override
    public CompletableFuture<File> getFileAsync(FTPFile ftpFile, String localFilePath) throws FTPConnectionException {
        return ftpConnection.retrAsync(ftpFile.getPath(), localFilePath);
    }

    @Override
    public void changeDirectory(FTPFile ftpFile) throws FTPConnectionException {
        ftpConnection.cwd(ftpFile.getPath());
    }

    @Override
    public void abort() throws FTPConnectionException {
        ftpConnection.abor();
    }

    @Override
    public String getRootDirectoryName(String path) throws FTPConnectionException {
        String directoryName = null;
        boolean success = ftpConnection.cwd(path);

        if (success) {
            success = ftpConnection.cdup();
            if (success) {
                directoryName = ftpConnection.pwd();
            }
        }

        return directoryName;
    }

    @Override
    public FTPConnectionReport testConnection(String host, int port, String userName, String userPassword) throws FTPConnectionException {
        FTPConnection testFTPConnection = new DefaultFTPConnection(new DefaultFTPConnector());
        FTPConnectionReport connectionReport = new FTPConnectionReport();

        connectionReport.setSuccessConnection(testFTPConnection.connect(host, port));
        testFTPConnection.user(userName);
        connectionReport.setSuccessUserLogin(testFTPConnection.pass(userPassword));
        connectionReport.setSuccessCommand(testFTPConnection.noop());
        connectionReport.setSuccessDisconnect(testFTPConnection.disconnect());

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
    public void reconnect() throws FTPConnectionException {
        ftpConnection.rein();
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