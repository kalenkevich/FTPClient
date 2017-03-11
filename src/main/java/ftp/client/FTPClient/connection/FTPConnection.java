package ftp.client.FTPClient.connection;

import ftp.client.FTPClient.file.FTPFile;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by a.kalenkevich on 16.02.2017.
 */
//TODO add javaDoc
public interface FTPConnection {
    boolean connect(String host, int port) throws IOException;
    boolean user(String userName) throws IOException;
    boolean pass(String pass) throws IOException;
    boolean quit() throws IOException;
    boolean rein() throws  IOException;
    boolean rein(String userName, String userPassword) throws  IOException;
    boolean disconnect() throws IOException;
    String pwd() throws IOException;
    boolean epsv() throws IOException;
    boolean pasv() throws IOException;
    boolean port() throws IOException;
    boolean abor() throws IOException;
    boolean noop() throws IOException;
    boolean stor(File file, String path) throws IOException;
    CompletableFuture<Boolean> storAsync(File file, String path);
    File retr(String remoteFilePath, String localFilePath) throws IOException;
    CompletableFuture<File> retrAsync(String remoteFilePath, String localFilepath);
    List<FTPFile> list(String pathname) throws IOException;
    CompletableFuture<List<FTPFile>> listAsync(String pathName);
    boolean bin() throws IOException;
    boolean ascii() throws IOException;
    boolean cwd(String dir) throws IOException;
    boolean cdup() throws IOException;
    boolean mkd(String path) throws IOException;
    boolean rmd(String path) throws IOException;
    boolean rnfr(String fileName) throws IOException;
    boolean rnto(String fileName) throws IOException;
    boolean dele(String filename) throws IOException;
    Date mdtm(String fileName) throws IOException;
    int size(String fileName) throws IOException;
    String syst() throws IOException;
    boolean type(char type) throws IOException;
    List<FTPFile> nlst(String pathName) throws IOException;
    boolean site(String arguments) throws IOException;
    void setLogger(Logger logger);
}
