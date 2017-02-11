package ftp.client.FTPClient;

/**
 * Created by alex on 1/29/2017.
 */

import org.apache.log4j.Logger;
import sample.service.FTPReply;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

import static java.lang.Integer.parseInt;

public class FTPConnection {
    private Logger logger;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public FTPConnection () {
        logger = Logger.getLogger(FTPConnection.class);
    }

    public synchronized void connect(String host) throws IOException {
        connect(host, 21);
    }

    public synchronized void connect(String host, int port) throws IOException {
        connect(host, port, "anonymous", "anonymous");
    }

    public synchronized void connect(String host, int port, String user,
                                     String pass) throws IOException {
        if (socket != null) {
            throw new IOException("SimpleFTP is already connected. Disconnect first.");
        }
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(
                 new OutputStreamWriter(socket.getOutputStream()));

        String response = null;
        if (skipWhileNotStartWith("220 ") == null) {
            throw new IOException(
                    "SimpleFTP received an unknown response when connecting to the FTP server: "
                            + response);
        }

        sendLine("USER " + user);

        if (skipWhileNotStartWith("331 ") == null) {
            throw new IOException(
                    "SimpleFTP received an unknown response after sending the user: "
                            + response);
        }

        sendLine("PASS " + pass);
        if (skipWhileNotStartWith("230 ") == null) {
            throw new IOException(
                    "SimpleFTP was unable to log in with the supplied password: "
                            + response);
        }
    }

    public synchronized void disconnect() throws IOException {
        try {
            sendLine("QUIT");
        } finally {
            socket = null;
        }
    }

    public synchronized String pwd() throws IOException {
        sendLine("PWD");
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

    public synchronized boolean stor(File file) throws IOException {
        if (file.isDirectory()) {
            throw new IOException("SimpleFTP cannot upload a directory.");
        }

        String filename = file.getName();

        return stor(new FileInputStream(file), filename);
    }

    public synchronized boolean stor(InputStream inputStream, String filename)
            throws IOException {

        BufferedInputStream fileInput = new BufferedInputStream(inputStream);
        sendLine("PASV");
        Socket dataSocket = getDataSocket();
        sendLine("STOR " + filename);
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

        return response.startsWith("226 ");
    }

    public synchronized boolean retrieveFile(String remoteFileName, OutputStream local) throws IOException {
        String response;
        if (socket == null) {
            throw new IOException("Not connected");
        }

        BufferedOutputStream clientOutput = new BufferedOutputStream(local);

        sendLine("RETR " + remoteFileName);
        Socket dataSocket = this.getDataSocket();

        BufferedInputStream serverInput = new BufferedInputStream(dataSocket.getInputStream());

        response = this.readLine();

        if (!response.startsWith ("150 ")) {
            throw new IOException("SimpleFTP was not allowed to send the file: "
                    + response);
        }

        byte[] buffer = new byte[4096];
        int readedBytesIndex = 0;

        while ((readedBytesIndex = serverInput.read(buffer)) != -1) {
            clientOutput.write(buffer, 0, readedBytesIndex);
        }

        clientOutput.write(buffer);
        clientOutput.flush();
        clientOutput.close();

        response = this.readLine();

        return response.startsWith("226 ");
    }

    public synchronized String list() throws IOException {
        sendLine("LIST");
        String response = readLine();
        if (!response.startsWith("227 ")) {
            throw new IOException("SimpleFTP could not request passive mode: "
                    + response);
        }

        Socket dataSocket = getDataSocket();
        BufferedInputStream input = new BufferedInputStream(dataSocket.getInputStream());
        StringBuilder data = new StringBuilder();
        char c = ' ';
        //todo alert
        while (c != '\n') {
            c = (char) input.read();
            data.append(c);
        }

        return data.toString();
    }

    private Socket getDataSocket() throws IOException {
        String response = readLine();
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

    public synchronized boolean bin() throws IOException {
        sendLine("TYPE I");
        String response = readLine();

        return (response.startsWith("200 "));
    }

    public synchronized boolean ascii() throws IOException {
        sendLine("TYPE A");
        String response = readLine();

        return (response.startsWith("200 "));
    }

    public synchronized boolean cwd(String dir) throws IOException {
        sendLine("CWD " + dir);
        String response = readLine();
        return (response.startsWith("250 "));
    }

    public synchronized String list(String pathname) throws IOException {
        sendLine("LIST " + pathname);

        String response = readLine();

        if (response.startsWith("257 ")) {
            int firstQuote = response.indexOf('\"');
            int secondQuote = response.indexOf('\"', firstQuote + 1);
            if (secondQuote > 0) {
                String list = response.substring(firstQuote + 1, secondQuote);

                return list;
            }
        }

        return response;
    }

    public synchronized boolean mkd(String path) throws IOException {
        sendLine("MKD " + path); // path? + /name

        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    public synchronized boolean rmd(String path) throws IOException {
        sendLine("RMD " + path);

        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    public synchronized boolean abor() throws IOException {
        sendLine("ABOR");

        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    public synchronized boolean dele(String pathname) throws IOException {
        sendLine("DELE");

        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    public synchronized boolean site(String arguments) throws IOException {
        sendLine("SITE" + arguments);

        String response = readLine();
        int statusCode  = getStatusCode(response);

        return FTPReply.isPositiveCompletion(statusCode);
    }

    public void sendLine(String line) throws IOException {
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

    public String readLine() throws IOException {
        String line = reader.readLine();
        logger.info("< " + line);

        return line;
    }

    public String readResponse() throws IOException {
        StringBuilder response = new StringBuilder();
        String line = readLine();
        //todo remove this shit
        while (!line.startsWith("214 ")) {
            response.append(line).append('\n');
            line = readLine();
        }

        return response.toString();
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    private int getStatusCode(String response) {
        return parseInt(response.substring(0, 2));
    }
}