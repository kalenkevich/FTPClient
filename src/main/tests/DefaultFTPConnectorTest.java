import ftp.client.FTPClient.connection.connector.DefaultFTPConnector;
import ftp.client.FTPClient.connection.connector.FTPConnector;

import ftp.client.FTPClient.transfer.FTPRequest;
import ftp.client.FTPClient.transfer.FTPResponse;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by a.kalenkevich on 30.03.2017.
 */
public class DefaultFTPConnectorTest {

    class StubInputStream extends InputStream {

        @Override
        public int read() throws IOException {
            return -1;
        }
    }

    class StubOutputStream extends OutputStream {

        @Override
        public void write(int b) throws IOException {

        }
    }

    @Test
    public void sendRequestTest() {
        try {
            FTPConnector spyFTPConnector = spy(new DefaultFTPConnector());
            FTPResponse successFTPResponse = new FTPResponse(200, "test response data");
            Socket spySocket = Mockito.mock(Socket.class);

            when(spySocket.getInputStream()).thenReturn(new StubInputStream());
            when(spySocket.getOutputStream()).thenReturn(new StubOutputStream());
            spyFTPConnector.setLogger(Logger.getLogger(DefaultFTPConnector.class));
            spyFTPConnector.setSocket(spySocket);
            when(spyFTPConnector.getResponse()).thenReturn(successFTPResponse);

            FTPResponse ftpResponseResult = spyFTPConnector.sendRequest(new FTPRequest("test command"));

            assertEquals(ftpResponseResult, successFTPResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getResponseTest() {

    }
}
