package ftp.client.FTPClient;

import ftp.client.FTPClient.user.User;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by alex on 2/8/2017.
 */
public class FTPClient {
    private FTPConnection ftpConnection;
    private User user;
    private Logger logger;

    public FTPClient() {
        logger = Logger.getLogger(FTPClient.class);
        ftpConnection = new FTPConnection();
        ftpConnection.setLogger(logger);
    }

    public String sendCommandLine(String line) {
        String response = null;
        try {
            ftpConnection.sendLine(line);
            response = ftpConnection.readResponse();
        } catch (IOException e) {
            logger.error(e);
        }

        return response;
    }

    public void login() {
        try {
            ftpConnection.connect(user.getHostName(), user.getPort(), user.getName(), user.getPassword());
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public FTPConnection getFtpConnection() {
        return ftpConnection;
    }

    public void setFtpConnection(FTPConnection ftpConnection) {
        this.ftpConnection = ftpConnection;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
