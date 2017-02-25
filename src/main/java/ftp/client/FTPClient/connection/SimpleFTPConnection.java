package ftp.client.FTPClient.connection;

import ftp.client.FTPClient.file.FTPFile;
import ftp.client.FTPClient.file.parser.engine.FTPFileParserEngine;
import ftp.client.FTPClient.service.FTPReply;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;
import static java.lang.Integer.parseInt;

/**
 * Created by a.kalenkevich on 16.02.2017.
 */
public class SimpleFTPConnection implements FTPConnection {
    private Logger logger;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private static final int MAX_CONNECTION_ATTEMPTS = 5;

    public SimpleFTPConnection () {
        logger = Logger.getLogger(SimpleFTPConnection.class);
    }

    @Override
    public synchronized boolean connect(String host, int port) throws IOException {
        if (socket != null) {
            throw new IOException("SimpleFTP is already connected. Disconnect first.");
        }
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        String response = readLine();
        int statusCode  = getStatusCode(response);
        int connectionAttempt = 0;

        while (statusCode != 220 && MAX_CONNECTION_ATTEMPTS >= connectionAttempt) {
            response = readLine();
            statusCode = getStatusCode(response);
            connectionAttempt++;
        }

        if (statusCode == 220) {
            return true;
        }

        throw new IOException("SimpleFTP received an unknown response when connecting to the FTP server: ");
    }

    @Override
    public boolean user(String userName) throws IOException {
        sendCommand("USER " + userName);
        String response = readLine();
        int statusCode  = getStatusCode(response);

        return statusCode == 331;
    }

    @Override
    public boolean pass(String pass) throws IOException {
        sendCommand("PASS " + pass);
        String response = readLine();
        int statusCode  = getStatusCode(response);

        if (statusCode == 530) {
            return false;
        } else if (statusCode == 230) {
            return true;
        }

        return false;
    }

    @Override
    public boolean quit() throws IOException {
        try {
            sendCommand("QUIT");
            socket.close();
        } catch (IOException e) {
            logger.error(e);

            return false;
        } finally {
            socket = null;
        }

        return true;
    }

    //TODO IMPLEMENT
    @Override
    public boolean rein() throws IOException {
        return false;
    }

    @Override
    public synchronized boolean disconnect() throws IOException {
        return quit();
    }

    @Override
    public synchronized String pwd() throws IOException {
        sendCommand("PWD");
        String dir = null;
        String response = readLine();
        int statusCode  = getStatusCode(response);

        if (statusCode == 200) {
            int firstQuote = response.indexOf('\"');
            int secondQuote = response.indexOf('\"', firstQuote + 1);
            if (secondQuote > 0) {
                dir = response.substring(firstQuote + 1, secondQuote);
            }
        }

        return dir;
    }

    @Override
    public boolean epsv() throws IOException {
        sendCommand("EPSV");
        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    @Override
    public boolean pasv() throws IOException {
        sendCommand("PASV");
        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    //TODO IMPLEMENT
    @Override
    public boolean port() throws IOException {
        sendCommand("PORT");
        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
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
    public File retr(String fileName) throws IOException {
        sendCommand("PORT");
        Socket dataSocket = getDataSocket();

        String fullPath = pwd() + "/" + fileName;
        String response;
        sendCommand("RETR " + fullPath);
        response = readLine();
        int statusCode  = getStatusCode(response);

        if (statusCode != 150) {
            throw new IOException("Unable to download file from the remote server");
        }

        File file = new File(fileName);
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
        sendCommand("PASV");

        Socket dataSocket = getDataSocket();
        sendCommand("STOR " + filename);
        String response = readLine();
        int statusCode  = getStatusCode(response);

        if (statusCode != 150) {
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

        response = readLine();
        statusCode = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    @Override
    public boolean cdup() throws IOException {
        sendCommand("CDUP");
        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
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
        sendCommand("CWD " + dir);

        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    @Override
    public synchronized List<FTPFile> list(String pathname) throws IOException {
        sendCommand("PASV");
        Socket dataSocket = getDataSocket();

        sendCommand("LIST " + pathname);
        String response = readLine();

        if (!response.startsWith ("150 ")) {
            throw new IOException("SimpleFTP was not allowed to send the file: "
                    + response);
        }

        FTPFileParserEngine engine = new FTPFileParserEngine(dataSocket.getInputStream(), pathname);
        List<FTPFile> ftpFiles = engine.getFiles();

        dataSocket.close();

        return ftpFiles;
    }

    //TODO IMPLEMENT
    @Override
    public List<FTPFile> nlst(String pathName) throws IOException {
        return null;
    }

    @Override
    public synchronized boolean mkd(String path) throws IOException {
        sendCommand("MKD " + path);

        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    @Override
    public synchronized boolean rmd(String path) throws IOException {
        sendCommand("RMD " + path);

        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    @Override
    public synchronized boolean abor() throws IOException {
        sendCommand("ABOR");

        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    @Override
    public synchronized boolean dele(String filename) throws IOException {
        sendCommand("DELE " + filename);

        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    //TODO IMPLEMENT
    @Override
    public String mdtm(String fileName) throws IOException {
        return null;
    }

    //TODO IMPLEMENT
    @Override
    public int size(String fileName) throws IOException {
        return 0;
    }

    @Override
    public String syst() throws IOException {
        sendCommand("SYST");

        return readLine();
    }

    @Override
    public boolean type(char type) throws IOException {
        sendCommand("TYPE " + type);

        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    @Override
    public synchronized boolean site(String arguments) throws IOException {
        sendCommand("SITE" + arguments);

        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    @Override
    public boolean rnfr(String fileName) throws IOException {
        sendCommand("RNFR " + fileName);

        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    @Override
    public boolean rnto(String fileName) throws IOException {
        sendCommand("RNTO " + fileName);

        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    @Override
    public boolean noop() throws IOException {
        sendCommand("NOOP");

        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    @Override
    public String readResponse() throws IOException {
        StringBuilder response = new StringBuilder();
        String line = readLine();
        int statusCode  = getStatusCode(line);

        while (statusCode != 214) {
            response.append(line).append('\n');
            line = readLine();
        }

        return response.toString();
    }

    @Override
    public boolean sendCommand(String line) throws IOException {
        if (socket == null) {
            throw new IOException("SimpleFTP is not connected.");
        }
        try {
            writer.write(line + "\r\n");
            writer.flush();
            logger.info("> " + line);
        } catch (IOException e) {
            socket = null;
            throw e;
        }

        return true;
    }

    private Socket getDataSocket() throws IOException {
        String response = readLine();
        int statusCode  = getStatusCode(response);

        while (statusCode != 227) {
            response = readLine();
            statusCode  = getStatusCode(response);
        }

        String ip = null;
        int port = -1;
        int opening = response.indexOf('(');
        int closing = response.indexOf(')', opening + 1);
        if (closing > 0) {
            String dataLink = response.substring(opening + 1, closing);
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

    private String readLine() throws IOException {
        String line = reader.readLine();
        logger.info("< " + line);

        return line;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    private int getStatusCode(String response) {
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
}