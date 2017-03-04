package ftp.client.FTPClient.transfer.config;

/**
 * Created by alex on 2/25/2017.
 */
public interface Command {
    String USER = "USER";
    String PASS = "PASS";
    String QUIT = "QUIT";
    String REIN = "REIN";
    String PWD = "PWD";
    String EPSV = "EPSV";
    String PASV = "PASV";
    String PORT = "PORT";
    String ABORT = "ABOR";
    String NOOP = "NOOP";
    String STOR = "STOR";
    String RETR = "RETR";
    String BIN = "I";
    String ASCII = "A";
    String CWD = "CWD";
    String CDUP = "CDUP";
    String MKD = "MKD";
    String RMD = "RMD";
    String RNFR = "RNFR";
    String RNTO = "RNTO";
    String DELE = "DELE";
    String MDTM = "MDTM";
    String SYST = "SYST";
    String SIZE = "SIZE";
    String TYPE = "TYPE";
    String LIST = "LIST";
    String NLST = "NLST";
    String SITE = "SIZE";
}
