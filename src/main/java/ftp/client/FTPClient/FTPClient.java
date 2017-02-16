package ftp.client.FTPClient;

import ftp.client.FTPClient.connection.FTPConnection;
import ftp.client.FTPClient.connection.SimpleFTPConnection;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by alex on 2/8/2017.
 */
public class FTPClient {
    private FTPConnection ftpConnection;
    private Logger logger;
    private String host;
    private int port;
    private String userName;
    private String password;

    public FTPClient() {
        logger = Logger.getLogger(FTPClient.class);
        ftpConnection = new SimpleFTPConnection();
        ftpConnection.setLogger(logger);
    }

    public FTPClient(String host, int port) {
        this();
        this.host = host;
        this.port = port;
    }

    public String sendCommandLine(String line) {
        String response = null;
        try {
            ftpConnection.sendCommand(line);
            response = ftpConnection.readResponse();
        } catch (IOException e) {
            logger.error(e);
        }

        return response;
    }

    public void login(String userName, String password) {
        this.userName = userName;
        this.password = password;
        reconnect();
    }

    public void reconnect() {
        try {
            ftpConnection.connect(host, port, userName, password);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public FTPConnection getFtpConnection() {
        return ftpConnection;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}