package ftp.client.FTPClient.connection;

import ftp.client.FTPClient.file.FTPFile;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by a.kalenkevich on 16.02.2017.
 */
//TODO add javaDoc
public interface FTPConnection {
    void connect(String host, int port, String user, String pass) throws IOException;
    void disconnect() throws IOException;
    String pwd() throws IOException;
    boolean stor(File file) throws IOException;
    boolean bin() throws IOException;
    boolean ascii() throws IOException;
    boolean cwd(String dir) throws IOException;
    List<FTPFile> list(String pathname) throws IOException;
    boolean mkd(String path) throws IOException;
    boolean rmd(String path) throws IOException;
    boolean abor() throws IOException;
    boolean dele(String filename) throws IOException;
    boolean site(String arguments) throws IOException;
    void sendCommand(String command)throws IOException;
    String readResponse() throws IOException;
    void setLogger(Logger logger);
}
