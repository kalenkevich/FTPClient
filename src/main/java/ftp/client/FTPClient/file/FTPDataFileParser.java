package ftp.client.FTPClient.file;

import java.util.List;

/**
 * Created by a.kalenkevich on 17.02.2017.
 */

//TODO add javaDoc
public interface FTPDataFileParser {
    List<FTPFile> parse(byte[] buffer);
}
