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
public interface FTPConnection {

    /**
     * Connect to FTPServer. Should be invoked before all command of this interface.
     * @param host name of the remote service
     * @param port number on the remote services' port.
     */
    boolean connect(String host, int port) throws IOException;

    /**
     * Send command 'user'  to the FTPServer with username parameter to the FTPServer
     * @param userName username of the user from FTPServer
     * @return a result of the command (if user exist returns true else false)
    */
    boolean user(String userName) throws IOException;

    /**
     * Send command 'pass'  to the FTPServer with pass parameter to the FTPServer. Use after user command.
     * @param pass password of the user from the FTPServer.
     * @return a result of the command (if password of the user is valid returns true else false)
     */
    boolean pass(String pass) throws IOException;

    /**
     * Send command 'quit'  to the FTPServer which told FTPServer to close connection.
     * @return a result of the command (if connection is closed returns true else false)
     */
    boolean quit() throws IOException;

    /**
     * Send command 'rein' to the FTPServer which told FTP to reconnect to the server with already known username and password.
     * @return a result of the command (if connection is closed returns true else false)
     */
    boolean rein() throws  IOException;

    /**
     * Send command 'rein' which told FTP to reconnect to the server with username and password parameters.
     * @param userName username of the user.
     * @param userPassword password of the user.
     * @return a result of the command (if connection is closed returns true else false)
     */
    boolean rein(String userName, String userPassword) throws  IOException;

    /**
     * Send command 'quit' which told FTPServer to close connection. (The same as 'quit' command)
     * @return a result of the command (if connection is closed returns true else false)
     */
    boolean disconnect() throws IOException;

    /**
     * Send command 'pwd' to the FTPServer which told him to return name of the working directory
     * @return name of the working directory.
     */
    String pwd() throws IOException;

    /**
     * Send command 'epsv' to the FTPServer which told him to switch to active mode
     * @return result of the command (if switch successfully returns true else false)
     */
    boolean epsv() throws IOException;

    /**
     * Send command 'pasv' to the FTPServer which told him to switch to passive mode
     * @return result of the command (if switch successfully returns true else false)
     */
    boolean pasv() throws IOException;

    /**
     * Send command 'port' to the FTPServer which told him to switch to port mode
     * @return result of the command (if switch successfully returns true else false)
     */
    boolean port() throws IOException;

    /**
     * Send command 'abor' to the FTPServer which told him to abort current command execution.
     * @return result of the command (if execution aborted successfully returns true else false)
     */
    boolean abor() throws IOException;

    /**
     * Send command 'noop' to the FTPServer which meaning no operation. (Used for testing of the connection)
     * @return result of the command (if connection successful returns true else false)
     */
    boolean noop() throws IOException;

    /**
     * Send command 'stor' to the FTPServer store the file into it's memory storage.
     * @param file file which should be stored in remote storage.
     * @param path path of the file. It's a path where file will be stored
     * @return result of the command (if stored successfully returns true else false)
     */
    boolean stor(File file, String path) throws IOException;

    /**
     * Send command 'stor' to the FTPServer store the file into it's memory storage.
     * Analog of the stor command, but do it in async mode.
     * @param file file which should be stored in remote storage.
     * @param path path of the file. It's a path where file will be stored
     * @return promise of the command's status.
     */
    CompletableFuture<Boolean> storAsync(File file, String path);

    /**
     * Send command 'retr' to the FTPServer store the file into it's memory storage.
     * @param remoteFilePath file path on the remove FTPServer which should be download.
     * @param localFilePath file path on the local machine where should be download.
     * @return result of the command (if downloaded successfully returns true else false)
     */
    File retr(String remoteFilePath, String localFilePath) throws IOException;

    /**
     * Send command 'retr' to the FTPServer store the file into it's memory storage.
     * Analog of the retr command, but do it in async mode.
     * @param remoteFilePath file path on the remove FTPServer which should be download.
     * @param localFilePath file path on the local machine where should be download.
     * @return promise of the command's status.
     */
    CompletableFuture<File> retrAsync(String remoteFilePath, String localFilePath);

    /**
     * Send command 'list' to the FTPServer which told it to return current directory files.
     * @param pathname path name of the directory.
     * @return list of files of the directory with such name.
     */
    List<FTPFile> list(String pathname) throws IOException;

    /**
     * Send command 'list' to the FTPServer which told it to return current directory files.
     * Analog path command, but do it in async mode.
     * @param pathName path name of the directory.
     * @return promise of the command's status.
     */
    CompletableFuture<List<FTPFile>> listAsync(String pathName);

    /**
     * Send command 'type I' to the FTPServer which told it to switch to the binary type of the files.
     * @return result of the command.
     */
    boolean bin() throws IOException;

    /**
     * Send command 'type A' to the FTPServer which told it to switch to the string type of the files.
     * @return result of the command.
     */
    boolean ascii() throws IOException;

    /**
     * Send command 'cwd' to the FTPServer which told it to change working directory to the directed be parameter.
     * @param dir path name of the directory on which will be changed.
     * @return result of the command.
     */
    boolean cwd(String dir) throws IOException;

    /**
     * Send command 'cdup' to the FTPServer which told it to change working directory to upper.
     * @return result of the command.
     */
    boolean cdup() throws IOException;

    /**
     * Send command 'mkd' to the FTPServer which told it to create new directory with name specified be the parameter.
     * @param path name of the new directory.
     * @return result of the command.
     */
    boolean mkd(String path) throws IOException;

    /**
     * Send command 'rmd' to the FTPServer which told it to remove directory with name specified be the parameter.
     * @param path name of the directory which should be removed.
     * @return result of the command.
     */
    boolean rmd(String path) throws IOException;

    /**
     * Send command 'rnfr' to the FTPServer which told it which file should be renamed.
     * @param fileName name of the new directory.
     * @return result of the command.
     */
    boolean rnfr(String fileName) throws IOException;

    /**
     * Send command 'rnto' to the FTPServer which told it the new name of the file which should be renamed.
     * Should be called after 'rnfr' command.
     * @param fileName name of the new directory.
     * @return result of the command.
     */
    boolean rnto(String fileName) throws IOException;

    /**
     * Send command 'dele' to the FTPServer which told it to delete file with the given name.
     * @param filename name of the file which should be removed.
     * @return result of the command.
     */
    boolean dele(String filename) throws IOException;

    /**
     * Send command 'mdtm' to the FTPServer which told it to return time of the file creation.
     * @param fileName name of the file.
     * @return time of the file creation.
     */
    Date mdtm(String fileName) throws IOException;

    /**
     * Send command 'size' to the FTPServer which told it to return size of the file.
     * @param fileName name of the file.
     * @return size of the file.
     */
    int size(String fileName) throws IOException;

    /**
     * Send command 'syst' to the FTPServer which told it to return type of the operation system where FTPServer is installed.
     * @return Type of the operation system.
     */
    String syst() throws IOException;

    /**
     * Send command 'type' to the FTPServer which told it to switch type of the file.
     * @return result of the command.
     */
    boolean type(char type) throws IOException;

    /**
     * Send command 'nlst' to the FTPServer which told it to return current directory files.
     * @return result list of the files.
     */
    List<FTPFile> nlst(String pathName) throws IOException;

    /**
     * Send command 'site' to the FTPServer which told ...
     * @return result list of the files.
     */
    boolean site(String arguments) throws IOException;

    /**
     * Sets a logger
     * @param logger a new logger object
     */
    void setLogger(Logger logger);
}
