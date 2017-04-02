import ftp.client.FTPClient.DefaultFTPClient;
import ftp.client.FTPClient.connection.DefaultFTPConnection;
import ftp.client.FTPClient.connection.connector.FTPConnectionException;
import ftp.client.FTPClient.connection.connector.FTPConnector;
import ftp.client.FTPClient.transfer.FTPRequest;
import ftp.client.FTPClient.transfer.FTPResponse;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by a.kalenkevich on 30.03.2017.
 */
public class DefaultFTPClientTest {

    private class FTPConnectorStub implements FTPConnector {
        private String message;
        @Override
        public FTPResponse sendRequest(FTPRequest ftpRequest) throws FTPConnectionException {
            this.message = ftpRequest.getCommand() + ftpRequest.getArguments();
            return getResponse();
        }

        @Override
        public FTPResponse getResponse() throws FTPConnectionException {
            return new FTPResponse(200, this.message);
        }

        @Override
        public void connect(String host, int port) throws IOException {

        }

        @Override
        public void disconnect() throws IOException {

        }

        @Override
        public void setLogger(Logger logger) {

        }
    }

    @Test
    public void ConnectionTest() {
        FTPConnector ftpConnector = new FTPConnectorStub();
        DefaultFTPClient defaultFTPClient = new DefaultFTPClient(new DefaultFTPConnection(ftpConnector));
        boolean connectionResult = false;

        try {
            defaultFTPClient.connect("localhost", 21);
            connectionResult = true;
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }

        assertEquals(connectionResult, true);
    }
}