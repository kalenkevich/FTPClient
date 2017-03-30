package ftp.client.FTPClient.file.parser.list;

import ftp.client.FTPClient.file.FTPFile;

import java.util.List;

/**
 * Created by a.kalenkevich on 17.02.2017.
 */

public interface FTPListFileParser {
    /**
     * Connects to ftp server
     * @param entries a list of entries which will be parsed and transformed to a list of the FTPFiles.
     * @return list of FTPFiles.
     */
    List<FTPFile> parse(List<String> entries);
}