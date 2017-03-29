package ftp.client.FTPClient.connection.connector;

import java.io.IOException;

/**
 * Created by a.kalenkevich on 27.03.2017.
 */
public class FTPConnectionException extends IOException {
    public FTPConnectionException(String message) {
        super(message);
    }
}
