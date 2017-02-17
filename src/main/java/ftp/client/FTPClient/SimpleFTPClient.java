package ftp.client.FTPClient;

import ftp.client.FTPClient.connection.FTPConnection;
import ftp.client.FTPClient.connection.SimpleFTPConnection;
import ftp.client.FTPClient.file.FTPFile;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 2/8/2017.
 */
public class SimpleFTPClient implements FTPClient {
    private FTPConnection ftpConnection;
    private Logger logger;
    private String host;
    private int port;
    private String userName;
    private String userPassword;
    private boolean isConnected;
    private int timeToLive;

    public SimpleFTPClient() {
        initDefaultValues();
    }

    private void initDefaultValues() {
        logger = Logger.getLogger(SimpleFTPClient.class);
        ftpConnection = new SimpleFTPConnection();
        ftpConnection.setLogger(logger);
        isConnected = false;
        timeToLive = 250;
    }

    public SimpleFTPClient(String host, int port) {
        this();
        this.host = host;
        this.port = port;
    }

    @Override
    public void login(String userName, String password) {
        this.userName = userName;
        this.userPassword = password;
        try {
            ftpConnection.connect(host, port, userName, password);
            isConnected = true;
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public void logout() {
        try {
            if (isConnected) {
                ftpConnection.disconnect();
                isConnected = false;
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public List<FTPFile> getDirectoryFiles(String path) {
        return new ArrayList<>();
    }

    @Override
    public void renameFile(FTPFile file, String newName) {
        String currentFileName = file.getPath();
        try {
            boolean success = ftpConnection.rnfr(currentFileName);
            if (success) {
                ftpConnection.rnto(newName);
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public void deleteFile(FTPFile file) {
        String filePath = file.getPath();
        try {
            ftpConnection.dele(filePath);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public void createFile(File file) {
        try {
            ftpConnection.stor(file);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public File getFile(FTPFile ftpFile) {
        File file = null;
        try {
            file = ftpConnection.retr(ftpFile.getPath());
        } catch (IOException e) {
            logger.error(e);
        }

        return file;
    }

    @Override
    public void changeDirectory(String path) {
        try {
            ftpConnection.cwd(path);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public void abort() {
        try {
            ftpConnection.abor();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public FTPFile getRootDirectoryName(String path) {
        FTPFile file = null;
        try {
            boolean success = ftpConnection.cwd(path);
            if (success) {
                success = ftpConnection.cdup();
                if (success) {
                    String directoryName = ftpConnection.pwd();
                    file = new FTPFile(directoryName);
                }
            }
        } catch (IOException e) {
            logger.error(e);
        }

        return file;
    }

    @Override
    public boolean testConnection(String host, int port, String userName, String userPassword) {
        FTPConnection testFTPConnection = new SimpleFTPConnection();
        boolean success = false;

        try {
            testFTPConnection.connect(host, port, userName, userPassword);
            success = testFTPConnection.noop();
            testFTPConnection.disconnect();
        } catch (IOException e) {
            logger.error(e);
        }

        return success;
    }

    @Override
    public String sendCommand(String line) {
        String response = null;
        try {
            ftpConnection.sendCommand(line);
            response = ftpConnection.readResponse();
        } catch (IOException e) {
            logger.error(e);
        }

        return response;
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

    private void reconnect() {
        login(userName, userPassword);
        logout();
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