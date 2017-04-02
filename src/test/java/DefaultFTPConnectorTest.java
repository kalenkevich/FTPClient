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

    private class StubOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {

        }
    }

    @Test
    public void sendRequestTest() {

        class StubInputStream extends InputStream {
            @Override
            public int read() throws IOException {
                return -1;
            }
        }

        try {
            Socket spySocket = Mockito.mock(Socket.class);
            when(spySocket.getInputStream()).thenReturn(new StubInputStream());
            when(spySocket.getOutputStream()).thenReturn(new StubOutputStream());

            FTPConnector spyFTPConnector = spy(new DefaultFTPConnector(spySocket));
            FTPResponse successFTPResponse = new FTPResponse(200, "test response data");
            FTPResponse ftpResponseResult;

            spyFTPConnector.setLogger(Logger.getLogger(DefaultFTPConnector.class));

            when(spyFTPConnector.getResponse()).thenReturn(successFTPResponse);
            ftpResponseResult = spyFTPConnector.sendRequest(new FTPRequest("test command"));
            assertEquals(ftpResponseResult, successFTPResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getResponseTest() {
        class StubInputStream extends InputStream {
            private int index;
            private byte[] bytes;

            StubInputStream(String data) {
                this.bytes = data.getBytes();
                index = 0;
            }

            @Override
            public int read() throws IOException {
                if (index < bytes.length) {
                    return bytes[index++];
                }

                return -1;
            }
        }

        try {
            Socket spySocket = Mockito.mock(Socket.class);
            when(spySocket.getInputStream()).thenReturn(new StubInputStream("200 test-data"));
            when(spySocket.getOutputStream()).thenReturn(new StubOutputStream());

            FTPConnector defaultFTPConnector = new DefaultFTPConnector(spySocket);
            FTPResponse ftpResponseResult;
            FTPResponse ftpResponseExpectedResult = new FTPResponse(200, "test-data");

            defaultFTPConnector.setLogger(Logger.getLogger(DefaultFTPConnector.class));

            ftpResponseResult = defaultFTPConnector.getResponse();
            assertEquals(ftpResponseResult.getData(), ftpResponseExpectedResult.getData());
            assertEquals(ftpResponseResult.getStatusCode(), ftpResponseExpectedResult.getStatusCode());

            ftpResponseExpectedResult = new FTPResponse(500, null, "test-error-message");
            when(spySocket.getInputStream()).thenReturn(new StubInputStream("500 test-error-message"));
            defaultFTPConnector = new DefaultFTPConnector(spySocket);

            ftpResponseResult = defaultFTPConnector.getResponse();
            assertEquals(ftpResponseResult.getData(), null);
            assertEquals(ftpResponseResult.getStatusCode(), ftpResponseExpectedResult.getStatusCode());
            assertEquals(ftpResponseResult.getErrorMessage(), ftpResponseExpectedResult.getErrorMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
