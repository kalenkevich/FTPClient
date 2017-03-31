package ftp.client.FTPClient.connection.connector;

import ftp.client.FTPClient.service.FTPReply;
import ftp.client.FTPClient.transfer.FTPRequest;
import ftp.client.FTPClient.transfer.FTPResponse;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;

import static java.lang.Integer.parseInt;

/**
 * Created by alex on 2/25/2017.
 */
public class DefaultFTPConnector implements FTPConnector {
    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket socket;
    private Logger logger;

    public DefaultFTPConnector() {
        logger = Logger.getLogger(DefaultFTPConnector.class);
    }

    public DefaultFTPConnector(String host, int port) throws IOException {
        this();
        this.socket = new Socket(host, port);
        initReaders();
    }

    public DefaultFTPConnector(Socket socket) throws FTPConnectionException {
        this();
        this.socket = socket;
        initReaders();
    }

    private void initReaders() throws FTPConnectionException {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            throw new FTPConnectionException("Error was occurred, while reading connecting to remote server");
        }
    }

    @Override
    public FTPResponse sendRequest(FTPRequest ftpRequest) throws FTPConnectionException {
        boolean success = sendRequest(ftpRequest.toString());
        FTPResponse response = null;
        if (success) {
            response = getResponse();
        }

        return response;
    }

    private boolean sendRequest(String request) throws FTPConnectionException {
        if (socket == null) {
            throw new FTPConnectionException("FTP is not connected.");
        }

        try {
            write(request);

            return true;
        } catch (IOException e) {
            socket = null;
            throw new FTPConnectionException("Error was occurred, while sending request" + request);
        }
    }

    @Override
    public FTPResponse getResponse() throws FTPConnectionException {
        try {
            String response = read();

            return parseResponse(response);
        } catch (IOException e) {
            throw new FTPConnectionException("Error was occurred, while reading response");
        }
    }

    @Override
    public void connect(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        initReaders();
    }

    @Override
    public void disconnect() throws IOException {
        if (this.socket != null) {
            this.socket.close();
        }
    }

    private FTPResponse parseResponse(String responseData) throws FTPConnectionException {
        int statusCode = getResponseStatusCode(responseData);
        String data = getResponseData(responseData);

        FTPResponse ftpResponse;
        boolean isErrorCode = isErrorCode(statusCode);

        if (isErrorCode) {
            ftpResponse = new FTPResponse(statusCode, null);
            ftpResponse.setError(true);
            ftpResponse.setErrorMessage(data);
        } else {
            ftpResponse = new FTPResponse(statusCode, data);
        }

        return ftpResponse;
    }

    private void write(String request) throws IOException {
        if (writer != null) {
            writer.write(request + "\r\n");
            writer.flush();
            logger.info("> " + request);
        }
    }

    private String read() throws IOException {
        String response;

        if (reader != null) {
            response = reader.readLine();

            if (response != null) {
                logger.info("< " + response);
            }

            return response;
        }

        return null;
    }

    private boolean isErrorCode(int statusCode) {
        return FTPReply.isNegativePermanent(statusCode);
    }

    private int getResponseStatusCode(String response) {
        int statusCode = -1;

        try {
            int parsedStatusCode = parseInt(response.substring(0, 3));
            boolean isStatusCode = response.charAt(3) == ' ';
            if (isStatusCode) {
                statusCode = parsedStatusCode;
            }
        } catch (Exception e) {
            logger.error(e);
        }

        return statusCode;
    }

    private String getResponseData(String response) throws FTPConnectionException {
        if (response == null) {
            return null;
        }

        if (response.length() >= 4) {
            return response.substring(4, response.length());
        }

        throw new FTPConnectionException("Invalid response from server");
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}