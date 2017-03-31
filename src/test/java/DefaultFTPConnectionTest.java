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
import java.util.Date;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by a.kalenkevich on 30.03.2017.
 */
public class DefaultFTPConnectionTest {

    private class StubSocket extends Socket {
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
    }

    private class StubFTPConnector implements FTPConnector {
        private Socket socket;
        private FTPResponse ftpResponse;

        StubFTPConnector(Socket socket) {
            this.socket = socket;
        }

        @Override
        public FTPResponse sendRequest(FTPRequest ftpRequest) throws FTPConnectionException {
            return getResponse();
        }

        @Override
        public FTPResponse getResponse() throws FTPConnectionException {
            return this.ftpResponse;
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
    public void userTest() {
        try {
            StubFTPConnector ftpConnector = Mockito.mock(StubFTPConnector.class);
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("USER", "test"))).thenReturn(new FTPResponse(331, "Password required for user"));
            boolean successes = ftpConnection.user("test");

            assertTrue(successes);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void passTest() {
        try {
            StubFTPConnector ftpConnector = Mockito.mock(StubFTPConnector.class);
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("USER", "test"))).thenReturn(new FTPResponse(331, "Password required for user"));
            boolean successes = ftpConnection.user("test");
            assertTrue(successes);

            when(ftpConnector.sendRequest(new FTPRequest("PASS", "test"))).thenReturn(new FTPResponse(230, "Logged on"));
            successes = ftpConnection.pass("test");
            assertTrue(successes);

            successes = ftpConnection.user("test");
            assertTrue(successes);

            when(ftpConnector.sendRequest(new FTPRequest("PASS", "wrong-pass"))).thenReturn(new FTPResponse(530, "Login or password incorrect!"));
            successes = ftpConnection.pass("wrong-pass");
            assertFalse(successes);

            when(ftpConnector.sendRequest(new FTPRequest("USER", "wrong-user"))).thenReturn(new FTPResponse(331, "Password required for user"));
            successes = ftpConnection.user("wrong-user");
            assertTrue(successes);

            when(ftpConnector.sendRequest(new FTPRequest("PASS", "test"))).thenReturn(new FTPResponse(530, "Login or password incorrect!"));
            successes = ftpConnection.pass("test");
            assertFalse(successes);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void quitTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("QUIT"))).thenReturn(new FTPResponse(221, "Goodbye"));

            boolean successes = ftpConnection.quit();
            assertTrue(successes);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void pwdTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("PWD"))).thenReturn(new FTPResponse(257, "\"test-directory-path\" is current directory."));

            String path = ftpConnection.pwd();
            assertEquals(path, "test-directory-path");
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void epsvTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("EPSV"))).thenReturn(new FTPResponse(200, "OK"));

            boolean successes = ftpConnection.epsv();
            assertTrue(successes);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void pasvTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("PASV"))).thenReturn(new FTPResponse(200, "OK"));

            boolean successes = ftpConnection.pasv();
            assertTrue(successes);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void aborTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("ABOR"))).thenReturn(new FTPResponse(200, "OK"));

            boolean successes = ftpConnection.abor();
            assertTrue(successes);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void noopTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("NOOP"))).thenReturn(new FTPResponse(200, "OK"));

            boolean successes = ftpConnection.noop();
            assertTrue(successes);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void storTest() {

    }


    @Test
    public void retrTest() {

    }

    @Test
    public void listTest() {

    }

    @Test
    public void binTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("TYPE", "I"))).thenReturn(new FTPResponse(200, "OK"));

            boolean successes = ftpConnection.bin();
            assertTrue(successes);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void asciiTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("TYPE", "A"))).thenReturn(new FTPResponse(200, "OK"));

            boolean successes = ftpConnection.ascii();
            assertTrue(successes);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void cwdTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("CWD", "test-directory"))).thenReturn(new FTPResponse(200, "OK"));

            boolean successes = ftpConnection.cwd("test-directory");
            assertTrue(successes);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void cdupTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("CDUP"))).thenReturn(new FTPResponse(200, "OK"));

            boolean successes = ftpConnection.cdup();
            assertTrue(successes);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void mkdTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("MKD", "test-new-directory"))).thenReturn(new FTPResponse(200, "OK"));

            boolean successes = ftpConnection.mkd("test-new-directory");
            assertTrue(successes);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void rmdTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("RMD", "test-deleted-directory"))).thenReturn(new FTPResponse(200, "OK"));

            boolean successes = ftpConnection.rmd("test-deleted-directory");
            assertTrue(successes);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void rnfrTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("RNFR", "file-from-rename"))).thenReturn(new FTPResponse(200, "OK"));

            boolean successes = ftpConnection.rnto("file-to-rename");
            assertTrue(successes);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void rntoTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("RNTO", "file-to-rename"))).thenReturn(new FTPResponse(200, "OK"));

            boolean successes = ftpConnection.rnto("file-to-rename");
            assertTrue(successes);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("RMD", "test-deleted-file"))).thenReturn(new FTPResponse(200, "OK"));

            boolean successes = ftpConnection.rmd("test-deleted-file");
            assertTrue(successes);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void mdtmTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("MDTM", "test-file-modification"))).thenReturn(new FTPResponse(200, "OK  20170331220005"));

            Date date = ftpConnection.mdtm("test-file-modification");
            assertEquals(date, new Date(2017, 3, 31, 22, 0, 5));
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sizeTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("SIZE", "test-file-size"))).thenReturn(new FTPResponse(200, "OK  123457"));

            int size = ftpConnection.size("test-file-size");
            assertEquals(size, 123457);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void systTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("SYST"))).thenReturn(new FTPResponse(200, "OK UNIX Type: L8"));

            String systemType = ftpConnection.syst();
            assertEquals(systemType, "UNIX");
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void typeTest() {
        try {
            StubFTPConnector ftpConnector = spy(new StubFTPConnector(new StubSocket("test-host", 200000)));
            FTPConnection ftpConnection = new DefaultFTPConnection(ftpConnector);
            when(ftpConnector.sendRequest(new FTPRequest("TYPE", "T"))).thenReturn(new FTPResponse(200, "OK"));

            boolean successes = ftpConnection.type('T');
            assertTrue(successes);
        } catch (FTPConnectionException e) {
            e.printStackTrace();
        }
    }
}
