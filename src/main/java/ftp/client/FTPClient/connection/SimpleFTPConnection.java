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

    public SimpleFTPConnection () {
        logger = Logger.getLogger(SimpleFTPConnection.class);
    }

    @Override
    public synchronized void connect(String host, int port, String user, String pass) throws IOException {
        if (socket != null) {
            throw new IOException("SimpleFTP is already connected. Disconnect first.");
        }
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        if (skipWhileNotStartWith("220 ") == null) {
            throw new IOException("SimpleFTP received an unknown response when connecting to the FTP server: ");
        }

        user(user);
        pass(pass);
    }

    @Override
    public boolean user(String userName) throws IOException {
        sendCommand("USER " + userName);
        if (skipWhileNotStartWith("331 ") == null) {
            throw new IOException("SimpleFTP received an unknown response after sending the user: ");
        }

        return true;
    }

    @Override
    public boolean pass(String pass) throws IOException {
        sendCommand("PASS " + pass);
        if (skipWhileNotStartWith("230 ") == null) {
            throw new IOException("SimpleFTP was unable to log in with the supplied password");
        }

        return true;
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
    public synchronized void disconnect() throws IOException {
        quit();
    }

    @Override
    public synchronized String pwd() throws IOException {
        sendCommand("PWD");
        String dir = null;
        String response = readLine();
        if (response.startsWith("257 ")) {
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
        return false;
    }

    @Override
    public synchronized boolean stor(File file) throws IOException {
        if (file.isDirectory()) {
            throw new IOException("SimpleFTP cannot upload a directory.");
        }

        String filename = file.getName();

        return stor(new FileInputStream(file), filename);
    }

    //TODO IMPLEMENT
    @Override
    public File retr(String fileName) throws IOException {
        return null;
    }

    public synchronized boolean stor(InputStream inputStream, String filename)
            throws IOException {

        BufferedInputStream fileInput = new BufferedInputStream(inputStream);
        sendCommand("PASV");
        Socket dataSocket = getDataSocket();
        sendCommand("STOR " + filename);
        String response = readLine();
        if (!response.startsWith ("125 ")) {
            throw new IOException("SimpleFTP was not allowed to send the file: "
                    + response);
        }
        BufferedOutputStream output = new BufferedOutputStream(dataSocket.getOutputStream());
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = fileInput.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        output.flush();
        output.close();
        fileInput.close();

        response = readLine();
        int statusCode  = getStatusCode(response);

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

        FTPFileParserEngine engine = new FTPFileParserEngine(dataSocket.getInputStream());
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

    //TODO IMPLEMENT
    @Override
    public String syst() throws IOException {
        sendCommand("SYST");

        String response = readLine();

        return "";
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
        sendCommand("RNFR" + fileName);

        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    @Override
    public boolean rnto(String fileName) throws IOException {
        sendCommand("RNTO" + fileName);

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
        while (!line.startsWith("214 ")) {
            response.append(line).append('\n');
            line = readLine();
        }

        return response.toString();
    }

    @Override
    public void sendCommand(String line) throws IOException {
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
    }

    private Socket getDataSocket() throws IOException {
        String response = skipWhileNotStartWith("227 ");
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

    //todo refactoring
    private String skipWhileNotStartWith(String prefix) {
        String response;
        try {
            response = readLine();
        } catch (IOException e) {
            logger.error(e);

            return null;
        }

        while (!response.startsWith(prefix) || response.equals("")) {
            try {
                response = readLine();
            } catch (IOException e) {
                logger.error(e);

                return null;
            }
        }

        return response;
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
        return parseInt(response.substring(0, 2));
    }
}