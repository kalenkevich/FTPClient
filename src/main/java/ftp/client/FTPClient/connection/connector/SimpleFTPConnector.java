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

    public SimpleFTPConnector(Socket socket) throws IOException {
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public FTPResponse sendRequest(FTPRequest ftpRequest) throws IOException {
        boolean success = sendRequest(ftpRequest.toString());
        FTPResponse response = null;
        if (success) {
            response = getResponse();
        }

        return response;
    }

    public boolean sendRequest(String request) throws IOException {
        if (socket == null) {
            throw new IOException("FTP is not connected.");
        }
        try {
            writer.write(request + "\r\n");
            writer.flush();
            logger.info("> " + request);
        } catch (IOException e) {
            socket = null;
            throw e;
        }

        return true;
    }

    @Override
    public FTPResponse getResponse() throws IOException {
        String response = reader.readLine();
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

    private boolean isErrorCode(int code) {
        return false;
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