import ftp.client.FTPClient.connection.DefaultFTPConnection;
import ftp.client.FTPClient.connection.FTPConnection;
import ftp.client.FTPClient.connection.connector.FTPConnectionException;
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

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by a.kalenkevich on 30.03.2017.
 */
public class DefaultFTPConnectionTest {

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

    class StubOutputStream extends OutputStream {
        private int[] bytes;
        private Integer index;
        public StubOutputStream(int[] bytes, Integer index) {
            this.bytes = bytes;
        }

        @Override
        public void write(int b) throws IOException {
            this.bytes[index] = b;
        }
    }

    class StubSocket extends Socket {
        private InputStream inputStream;
        private OutputStream outputStream;

        StubSocket(String host, int port) {

        }

        @Override
        public InputStream getInputStream() throws IOException {
            return this.inputStream;
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return this.outputStream;
        }

        public void setInputStream(InputStream inputStream) throws IOException {
            this.inputStream = inputStream;
        }

        public void setOutputStream(OutputStream outputStream) throws IOException {
            this.outputStream = outputStream;
        }
    }

    class StubFTPConnector implements FTPConnector {
        private Socket socket;

        StubFTPConnector() {

        }

        @Override
        public FTPResponse sendRequest(FTPRequest ftpRequest) throws FTPConnectionException {
            return null;
        }

        @Override
        public FTPResponse getResponse() throws FTPConnectionException {
            return null;
        }

        @Override
        public void setSocket(Socket socket) throws FTPConnectionException {
            this.socket = socket;
        }

        @Override
        public void setLogger(Logger logger) {

        }
    }

    @Test
    public void connect() {

    }

    @Test
    public void userTest() {
        try {
            FTPConnector ftpConnector = Mockito.mock(StubFTPConnector.class);
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("USER", "test"))).thenReturn(new FTPResponse(331, "Password required for use"));
            boolean successes = ftpConnection.user("test");

            assertTrue(successes);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void passTest() {

    }

    @Test
    public void quitTest() {

    }

    @Test
    public void reinTest() {

    }

    @Test
    public void disconnectTest() {

    }

    @Test
    public void pwdTest() {

    }

    @Test
    public void epsvTest() {

    }

    @Test
    public void pasvTest() {

    }

    @Test
    public void portTest() {

    }

    @Test
    public void aborTest() {

    }

    @Test
    public void noopTest() {

    }

    @Test
    public void storTest() {

    }

    @Test
    public void storAsyncTest() {

    }

    @Test
    public void retrTest() {

    }

    @Test
    public void retrAsyncTest() {

    }

    @Test
    public void listTest() {

    }

    @Test
    public void listAsyncTest() {

    }

    @Test
    public void binTest() {

    }

    @Test
    public void asciiTest() {

    }

    @Test
    public void cwdTest() {

    }

    @Test
    public void cdupTest() {

    }

    @Test
    public void mkdTest() {

    }

    @Test
    public void rmdTest() {

    }

    @Test
    public void rnfrTest() {

    }

    @Test
    public void rntoTest() {

    }

    @Test
    public void deleTest() {

    }

    @Test
    public void mdtmTest() {

    }

    @Test
    public void sizeTest() {

    }

    @Test
    public void systTest() {

    }

    @Test
    public void typeTest() {

    }

    @Test
    public void nlstTest() {

    }

    @Test
    public void siteTest() {

    }
}
