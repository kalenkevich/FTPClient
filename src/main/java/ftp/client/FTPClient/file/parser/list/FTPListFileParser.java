package ftp.client.FTPClient.file.parser.list;

import ftp.client.FTPClient.file.FTPFile;

import java.util.List;

/**
 * Created by a.kalenkevich on 17.02.2017.
 */

//TODO add javaDoc
public interface FTPListFileParser {
    List<FTPFile> parse(List<String> entries);
}