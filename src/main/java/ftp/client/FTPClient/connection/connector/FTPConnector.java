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
     * Set socket for this connector.
     */
    void setSocket(Socket socket) throws FTPConnectionException;

    /**
     * Read data from socket stream.
     * @return data from socket.
     */
    String read() throws IOException;

    /**
     * Write data to the socket stream.
     * @param request data which should be to socket stream.
     */
    void write(String request) throws IOException;

    void setLogger(Logger logger);
}
