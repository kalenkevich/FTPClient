package ftp.client.FTPClient.connection.connector;

import ftp.client.FTPClient.transfer.FTPRequest;
import ftp.client.FTPClient.transfer.FTPResponse;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by alex on 2/25/2017.
 */
public interface FTPConnector {
    FTPResponse sendRequest(FTPRequest ftpRequest) throws IOException;
    FTPResponse getResponse() throws IOException;
    void setLogger(Logger logger);
}
