package ftp.client.FTPClient.connection;

import ftp.client.FTPClient.connection.connector.DefaultFTPConnector;
import ftp.client.FTPClient.connection.connector.FTPConnectionException;
import ftp.client.FTPClient.connection.connector.FTPConnector;
import ftp.client.FTPClient.file.FTPFile;
import ftp.client.FTPClient.file.parser.list.engine.FTPListFileParserEngine;
import ftp.client.FTPClient.service.FTPReply;
import ftp.client.FTPClient.transfer.FTPRequest;
import ftp.client.FTPClient.transfer.FTPResponse;
import ftp.client.FTPClient.transfer.config.Command;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CompletableFuture;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.parseUnsignedInt;

/**
 * Created by a.kalenkevich on 16.02.2017.
 */
public class DefaultFTPConnection implements FTPConnection {
    private Logger logger;
    private Socket socket;
    private static final int MAX_CONNECTION_ATTEMPTS = 5;
    private String currentUserName;
    private String currentUserPassword;
    private String currentHost;
    private int currentPort;
    private FTPConnector ftpConnector;

    public DefaultFTPConnection(FTPConnector ftpConnector) {
        this.ftpConnector = ftpConnector;
        logger = Logger.getLogger(DefaultFTPConnection.class);
    }

    @Override
    public synchronized boolean connect(String host, int port) throws FTPConnectionException {
        setUpSocket(host, port);
        setUpFRPConnector();

        int statusCode = ftpConnector.getResponse().getStatusCode();
        int connectionAttempt = 0;

        while (statusCode != 220 && MAX_CONNECTION_ATTEMPTS >= connectionAttempt) {
            statusCode = ftpConnector.getResponse().getStatusCode();
            connectionAttempt++;
        }

        if (FTPReply.isPositiveCompletion(statusCode)) {
            return true;
        }

        throw new FTPConnectionException("SimpleFTP received an unknown response when connecting to the FTP server:");
    }

    private void setUpSocket(String host, int port) throws FTPConnectionException {
        if (socket != null) {
            throw new FTPConnectionException("SimpleFTP is already connected. Disconnect first.");
        }
        try {
            this.currentHost = host;
            this.currentPort = port;
            socket = new Socket(host, port);
        } catch (IOException e) {
            throw new FTPConnectionException("Can't connect to remote server:" + host + ':' + port);
        }
    }

    private void setUpFRPConnector() throws FTPConnectionException {
        if (ftpConnector != null) {
            ftpConnector.setSocket(socket);
        } else {
            ftpConnector = new DefaultFTPConnector(socket);
        }

        ftpConnector.setLogger(logger);
    }

    @Override
    public boolean user(String userName) throws FTPConnectionException {
        currentUserName = userName;
        FTPResponse response = sendRequest(new FTPRequest(Command.USER, userName));

        return response.getStatusCode() == 331;
    }

    @Override
    public boolean pass(String pass) throws FTPConnectionException {
        currentUserPassword = pass;
        FTPResponse response = sendRequest(new FTPRequest(Command.PASS, pass));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public boolean quit() throws FTPConnectionException {
        try {
            sendRequest(new FTPRequest(Command.QUIT));
            socket.close();
        } catch (IOException e) {
            logger.error(e);

            return false;
        } finally {
            socket = null;
        }

        return true;
    }

    @Override
    public boolean rein() throws FTPConnectionException {
        return rein(currentUserName, currentUserPassword);
    }

    @Override
    public boolean rein(String userName, String userPassword) throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.REIN));

        int statusCode = response.getStatusCode();
        if (!FTPReply.isPositiveCompletion(statusCode)) {
            throw new FTPConnectionException("Can't reconnect to server");
        }
        boolean success = user(userName);
        if (success) {
            success = pass(userPassword);
        }

        return success;
    }

    @Override
    public synchronized boolean disconnect() throws FTPConnectionException {
        return quit();
    }

    @Override
    public synchronized String pwd() throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.PWD));
        String dir = null;
        int statusCode = response.getStatusCode();
        String data = response.getData();

        if (FTPReply.isPositiveCompletion(statusCode)) {
            int firstQuote = data.indexOf('\"');
            int secondQuote = data.indexOf('\"', firstQuote + 1);
            if (secondQuote > 0) {
                dir = data.substring(firstQuote + 1, secondQuote);
            }
        }

        return dir;
    }

    @Override
    public boolean epsv() throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.EPSV));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public boolean pasv() throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.PASV));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public boolean port() throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.PORT));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public synchronized boolean stor(File file, String path) throws FTPConnectionException, FileNotFoundException {
        if (file.isDirectory()) {
            throw new FTPConnectionException("SimpleFTP cannot upload a directory.");
        }

        String newFilePath = path + "/" + file.getName();

        return stor(new FileInputStream(file), newFilePath);
    }

    @Override
    public CompletableFuture<Boolean> storAsync(File file, String path) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return stor(file, path);
            } catch (FTPConnectionException | FileNotFoundException e) {
                logger.error(e);
            }

            return false;
        });
    }

    @Override
    public File retr(String remoteFilePath, String localFilePath) throws FTPConnectionException {
        Socket dataSocket = getDataSocket();
        FTPResponse response = sendRequest(new FTPRequest(Command.RETR, remoteFilePath));

        if (!FTPReply.isPositivePreliminary(response.getStatusCode())) {
            throw new FTPConnectionException("Unable to download file from the remote server");
        }

        File file = new File(localFilePath);
        BufferedInputStream input;

        try {
            input = new BufferedInputStream(dataSocket.getInputStream());
            BufferedOutputStream output;
            output = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buffer = new byte[4096];
            int bytesRead;

            logger.info("Downloading file ...");

                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(bytesRead);
                    output.flush();
                }

            logger.info("File was downloaded");
            input.close();
            output.close();
        } catch (IOException e) {
            throw new FTPConnectionException("Unable to download file from the remote server");
        }

        return file;
    }

    @Override
    public CompletableFuture<File> retrAsync(String remoteFilePath, String localFilePath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return retr(remoteFilePath, localFilePath);
            } catch (FTPConnectionException e) {
                logger.error(e);
            }

            return null;
        });
    }

    private synchronized boolean stor(InputStream inputStream, String filename) throws FTPConnectionException {
        BufferedInputStream fileInput = new BufferedInputStream(inputStream);
        Socket dataSocket = getDataSocket();
        FTPResponse response = sendRequest(new FTPRequest(Command.STOR, filename));

        if (!FTPReply.isPositivePreliminary(response.getStatusCode())) {
            throw new FTPConnectionException("SimpleFTP was not allowed to send the file: " + response);
        }

        BufferedOutputStream output = null;

        try {
            output = new BufferedOutputStream(dataSocket.getOutputStream());
            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            logger.info("Uploading file ...");
            while ((bytesRead = fileInput.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
                output.flush();
            }
            logger.info("File was uploaded");
            output.close();
            fileInput.close();
        } catch (IOException e) {
            throw new FTPConnectionException("Unable to upload file to the remote server");
        }

        return FTPReply.isPositiveCompletion(ftpConnector.getResponse().getStatusCode());
    }

    @Override
    public boolean cdup() throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.CDUP));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public synchronized boolean bin() throws FTPConnectionException {
        return type('I');
    }

    @Override
    public synchronized boolean ascii() throws FTPConnectionException {
        return type('A');
    }

    @Override
    public synchronized boolean cwd(String dir) throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.CWD, dir));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public synchronized List<FTPFile> list(String pathName) throws FTPConnectionException {
        Socket dataSocket = getDataSocket();

        FTPResponse response = sendRequest(new FTPRequest(Command.LIST, pathName));

        if (!FTPReply.isPositivePreliminary(response.getStatusCode())) {
            throw new FTPConnectionException(response.getErrorMessage());
        }

        List<FTPFile> ftpFiles;

        try {
            logger.info("Downloading list of files");
            FTPListFileParserEngine engine = new FTPListFileParserEngine(dataSocket.getInputStream(), pathName);
            ftpFiles = engine.getFiles();
            logger.info("List of files was downloaded");
            dataSocket.close();
        } catch (IOException e) {
            throw new FTPConnectionException("Can't get list of files of the directory:" + pathName);
        }

        return ftpFiles;
    }

    @Override
    public CompletableFuture<List<FTPFile>> listAsync(String pathName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return list(pathName);
            } catch (FTPConnectionException e) {
                logger.error(e);
            }

            return null;
        });
    }

    @Override
    public List<FTPFile> nlst(String pathName) throws FTPConnectionException {
        Socket dataSocket = getDataSocket();

        FTPResponse response = sendRequest(new FTPRequest(Command.NLST, pathName));

        if (!FTPReply.isPositivePreliminary(response.getStatusCode())) {
            throw new FTPConnectionException(response.getErrorMessage());
        }

        List<FTPFile> ftpFiles;
        try {
            FTPListFileParserEngine engine = new FTPListFileParserEngine(dataSocket.getInputStream(), pathName);
            ftpFiles = engine.getFiles();
            dataSocket.close();
        } catch (IOException e) {
            throw new FTPConnectionException("Can't get list of files of the directory:" + pathName);
        }

        return ftpFiles;
    }

    @Override
    public synchronized boolean mkd(String path) throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.MKD, path));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public synchronized boolean rmd(String path) throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.RMD, path));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public synchronized boolean abor() throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.ABORT));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public synchronized boolean dele(String filename) throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.DELE, filename));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public Date mdtm(String fileName) throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.CWD, fileName));
        String data = response.getData();
        Date date;

        if (FTPReply.isPositiveCompletion(response.getStatusCode())) {
            int year = parseInt(data.substring(4, 8));
            int month = parseInt(data.substring(8, 10));
            int day = parseInt(data.substring(10, 12));
            int hour = parseInt(data.substring(12, 14));
            int minute = parseInt(data.substring(14, 16));
            int second = parseInt(data.substring(16, 18));
            date = new Date(year, month, day, hour, minute, second);
        } else {
            throw new FTPConnectionException("");
        }

        return date;
    }

    @Override
    public int size(String fileName) throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.SIZE, fileName));
        String data = response.getData();
        int fileSize;

        if (FTPReply.isPositiveCompletion(response.getStatusCode())) {
            fileSize = parseInt(data.substring(4, data.length()));
        } else {
            throw new FTPConnectionException("");
        }

        return fileSize;
    }

    @Override
    public String syst() throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.SYST));

        return response.getData();
    }

    @Override
    public boolean type(char type) throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.TYPE, String.valueOf(type)));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public synchronized boolean site(String arguments) throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.SITE, arguments));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public boolean rnfr(String fileName) throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.RNFR, fileName));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public boolean rnto(String fileName) throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.RNTO, fileName));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public boolean noop() throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.NOOP));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    private Socket getDataSocket() throws FTPConnectionException {
        FTPResponse response = sendRequest(new FTPRequest(Command.PASV));
        int statusCode = response.getStatusCode();

        while (statusCode != 227) {
            response = ftpConnector.getResponse();
            statusCode = response.getStatusCode();
        }

        String data = response.getData();

        String ip = null;
        int port = -1;
        int opening = data.indexOf('(');
        int closing = data.indexOf(')', opening + 1);
        if (closing > 0) {
            String dataLink = data.substring(opening + 1, closing);
            StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
            try {
                ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
                        + tokenizer.nextToken() + "." + tokenizer.nextToken();
                port = parseInt(tokenizer.nextToken()) * 256
                        + parseInt(tokenizer.nextToken());
            } catch (Exception e) {
                throw new FTPConnectionException("SimpleFTP received bad data link information: " + response);
            }
        }

        try {
            return new Socket(ip, port);
        } catch (IOException e) {
            throw new FTPConnectionException("Can't connect to remote server ");
        }
    }

    private FTPResponse sendRequest(FTPRequest ftpRequest) throws FTPConnectionException {
        FTPResponse ftpResponse;

        ftpResponse = ftpConnector.sendRequest(ftpRequest);
        if (wasDisconnected(ftpResponse)) {
            rein();
            ftpResponse = ftpConnector.sendRequest(ftpRequest);
        }

        return ftpResponse;
    }

    private boolean wasDisconnected(FTPResponse response) {
        return response != null && response.getStatusCode() == 421;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
        ftpConnector.setLogger(logger);
    }
}