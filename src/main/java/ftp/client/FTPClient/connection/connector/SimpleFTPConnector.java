package ftp.client.FTPClient.connection.connector;

import ftp.client.FTPClient.transfer.FTPRequest;
import ftp.client.FTPClient.transfer.FTPResponse;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;

import static java.lang.Integer.parseInt;

/**
 * Created by alex on 2/25/2017.
 */
public class SimpleFTPConnector implements FTPConnector {
    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket socket;
    private Logger logger;

    public SimpleFTPConnector() {

    }

    public SimpleFTPConnector(Socket socket) throws FTPConnectionException {
        this.socket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new FTPConnectionException("Error was occurred, while reading connecting to remote server");
        }
        try {
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

    public boolean sendRequest(String request) throws FTPConnectionException {
        if (socket == null) {
            throw new FTPConnectionException("FTP is not connected.");
        }
        try {
            writer.write(request + "\r\n");
            writer.flush();
            logger.info("> " + request);
        } catch (IOException e) {
            socket = null;
            throw new FTPConnectionException("Error was occurred, while sending request" + request);
        }

        return true;
    }

    @Override
    public FTPResponse getResponse() throws FTPConnectionException {
        String response = null;
        try {
            response = reader.readLine();
        } catch (IOException e) {
            throw new FTPConnectionException("Error was occurred, while reading response");
        }
        logger.info("< " + response);

        int statusCode = getResponseStatusCode(response);
        String data = getResponseData(response);

        FTPResponse ftpResponse = null;
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

    //todo implement
    private boolean isErrorCode(int statusCode) {
        return false;
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

    private String getResponseData(String response) {
        return response.substring(4, response.length());
    }

    public BufferedReader getReader() {
        return reader;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public BufferedWriter getWriter() {
        return writer;
    }

    public void setWriter(BufferedWriter writer) {
        this.writer = writer;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}