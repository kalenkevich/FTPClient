package ftp.client.FTPClient.connection.connector;

import ftp.client.FTPClient.transfer.FTPRequest;
import ftp.client.FTPClient.transfer.FTPResponse;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by alex on 2/25/2017.
 */
public interface FTPConnector {
    /**
     * Send FTPRequest to the FTPServer.
     * @param ftpRequest the request which should  be send.
     * @return response from the FTPServer.
     */
    FTPResponse sendRequest(FTPRequest ftpRequest) throws FTPConnectionException;

    /**
     * Send FTPRequest to the FTPServer.
     * @return last response from the FTPServer.
     */
    FTPResponse getResponse() throws FTPConnectionException;

    /**
     * Connect to socket be the current address
     * @param host name of the remove machine to connect
     * @param port port number of the remote machine to connect
     */
    void connect(String host, int port) throws IOException;

    /**
     * Close active connection
     */
    void disconnect() throws IOException;

    void setLogger(Logger logger);
}
