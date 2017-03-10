package ftp.client.FTPClient.connection;

import ftp.client.FTPClient.connection.connector.FTPConnector;
import ftp.client.FTPClient.connection.connector.SimpleFTPConnector;
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
import static java.lang.Integer.parseInt;
import static java.lang.Integer.parseUnsignedInt;

/**
 * Created by a.kalenkevich on 16.02.2017.
 */
public class SimpleFTPConnection implements FTPConnection {
    private Logger logger;
    private Socket socket;
    private static final int MAX_CONNECTION_ATTEMPTS = 5;
    private String currentUserName;
    private String currentUserPassword;
    private FTPConnector ftpConnector;

    public SimpleFTPConnection () {
        ftpConnector = new SimpleFTPConnector();
        logger = Logger.getLogger(SimpleFTPConnection.class);
    }

    @Override
    public synchronized boolean connect(String host, int port) throws IOException {
        if (socket != null) {
            throw new IOException("SimpleFTP is already connected. Disconnect first.");
        }
        socket = new Socket(host, port);
        ftpConnector = new SimpleFTPConnector(socket);

        int statusCode = ftpConnector.getResponse().getStatusCode();
        int connectionAttempt = 0;

        while (statusCode != 220 && MAX_CONNECTION_ATTEMPTS >= connectionAttempt) {
            statusCode = ftpConnector.getResponse().getStatusCode();
            connectionAttempt++;
        }

        if (statusCode == 220) {
            return true;
        }

        throw new IOException("SimpleFTP received an unknown response when connecting to the FTP server: ");
    }

    @Override
    public boolean user(String userName) throws IOException {
        currentUserName = userName;
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.USER, userName));

        return response.getStatusCode() == 331;
    }

    @Override
    public boolean pass(String pass) throws IOException {
        currentUserPassword = pass;
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.PASS, pass));

        return response.getStatusCode() == 230;
    }

    @Override
    public boolean quit() throws IOException {
        try {
            ftpConnector.sendRequest(new FTPRequest(Command.QUIT, ""));
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
    public boolean rein() throws IOException {
        return rein(currentUserName, currentUserPassword);
    }

    @Override
    public boolean rein(String userName, String userPassword) throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.REIN, ""));

        int statusCode = response.getStatusCode();
        if (statusCode != 200) {
            throw new IOException("Can't reconnect to server");
        }
        boolean success = user(userName);
        if (success) {
            success = pass(userPassword);
        }

        return success;
    }

    @Override
    public synchronized boolean disconnect() throws IOException {
        return quit();
    }

    @Override
    public synchronized String pwd() throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.PWD, ""));
        String dir = null;
        int statusCode = response.getStatusCode();
        String data = response.getData();

        if (statusCode == 200) {
            int firstQuote = data.indexOf('\"');
            int secondQuote = data.indexOf('\"', firstQuote + 1);
            if (secondQuote > 0) {
                dir = data.substring(firstQuote + 1, secondQuote);
            }
        }

        return dir;
    }

    @Override
    public boolean epsv() throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.EPSV, ""));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public boolean pasv() throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.PASV, ""));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public boolean port() throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.PORT, ""));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public synchronized boolean stor(File file, String path) throws IOException {
        if (file.isDirectory()) {
            throw new IOException("SimpleFTP cannot upload a directory.");
        }

        String newFilePath = path + "/" + file.getName();

        return stor(new FileInputStream(file), newFilePath);
    }

    @Override
    public File retr(String remoteFilePath, String localFilePath) throws IOException {
        Socket dataSocket = getDataSocket();
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.RETR, remoteFilePath));

        if (response.getStatusCode() != 150) {
            throw new IOException("Unable to download file from the remote server");
        }

        File file = new File(localFilePath);
        BufferedInputStream input = new BufferedInputStream(dataSocket.getInputStream());
        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));

        byte[] buffer = new byte[4096];
        int bytesRead = 0;

        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(bytesRead);
            output.flush();
        }
        input.close();
        output.close();

        return file;
    }

    public synchronized boolean stor(InputStream inputStream, String filename) throws IOException {
        BufferedInputStream fileInput = new BufferedInputStream(inputStream);
        Socket dataSocket = getDataSocket();
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.STOR, filename));

        if (response.getStatusCode() != 150) {
            throw new IOException("SimpleFTP was not allowed to send the file: " + response);
        }
        BufferedOutputStream output = new BufferedOutputStream(dataSocket.getOutputStream());
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = fileInput.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
            output.flush();
        }
        output.close();
        fileInput.close();

        return FTPReply.isPositiveCompletion(ftpConnector.getResponse().getStatusCode());
    }

    @Override
    public boolean cdup() throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.CDUP, ""));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public synchronized boolean bin() throws IOException {
        return type('I');
    }

    @Override
    public synchronized boolean ascii() throws IOException {
        return type('A');
    }

    @Override
    public synchronized boolean cwd(String dir) throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.CWD, dir));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public synchronized List<FTPFile> list(String pathName) throws IOException {
        Socket dataSocket = getDataSocket();

        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.LIST, pathName));
        int statusCode = response.getStatusCode();
        if (statusCode != 150) {
            throw new IOException("SimpleFTP was not allowed to send the file: "
                    + response);
        }

        FTPListFileParserEngine engine = new FTPListFileParserEngine(dataSocket.getInputStream(), pathName);
        List<FTPFile> ftpFiles = engine.getFiles();

        dataSocket.close();

        return ftpFiles;
    }

    //TODO IMPLEMENT
    @Override
    public List<FTPFile> nlst(String pathName) throws IOException {
        Socket dataSocket = getDataSocket();

        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.NLST, pathName));
        int statusCode = response.getStatusCode();

        if (statusCode != 150) {
            {
                throw new IOException("SimpleFTP was not allowed to send the file: "
                        + response);
            }
        }

        FTPListFileParserEngine engine = new FTPListFileParserEngine(dataSocket.getInputStream(), pathName);
        List<FTPFile> ftpFiles = engine.getFiles();

        dataSocket.close();

        return ftpFiles;
    }

    @Override
    public synchronized boolean mkd(String path) throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.MKD, path));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public synchronized boolean rmd(String path) throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.RMD, path));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public synchronized boolean abor() throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.ABORT, ""));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public synchronized boolean dele(String filename) throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.DELE, filename));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public Date mdtm(String fileName) throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.CWD, fileName));
        int statusCode = response.getStatusCode();
        String data = response.getData();
        Date date;

        if (statusCode == 213) {
            int year = parseInt(data.substring(4, 8));
            int month = parseInt(data.substring(8, 10));
            int day = parseInt(data.substring(10, 12));
            int hour = parseInt(data.substring(12, 14));
            int minute = parseInt(data.substring(14, 16));
            int second = parseInt(data.substring(16, 18));
            date = new Date(year, month, day, hour, minute, second);
        } else {
            throw new IOException("");
        }

        return date;
    }

    @Override
    public int size(String fileName) throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.SIZE, fileName));
        int statusCode = response.getStatusCode();
        String data = response.getData();
        int fileSize = -1;

        if (statusCode == 213) {
            fileSize = parseInt(data.substring(4, data.length()));
        } else {
            throw new IOException("");
        }

        return fileSize;
    }

    @Override
    public String syst() throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.SYST, ""));

        return response.getData();
    }

    @Override
    public boolean type(char type) throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.TYPE, String.valueOf(type)));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public synchronized boolean site(String arguments) throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.SITE, arguments));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public boolean rnfr(String fileName) throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.RNFR, fileName));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public boolean rnto(String fileName) throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.RNTO, fileName));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    @Override
    public boolean noop() throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.NOOP, ""));

        return FTPReply.isPositiveCompletion(response.getStatusCode());
    }

    private Socket getDataSocket() throws IOException {
        FTPResponse response = ftpConnector.sendRequest(new FTPRequest(Command.PASV, ""));
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
                throw new IOException("SimpleFTP received bad data link information: "
                        + response);
            }
        }

        return new Socket(ip, port);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
        ftpConnector.setLogger(logger);
    }
}