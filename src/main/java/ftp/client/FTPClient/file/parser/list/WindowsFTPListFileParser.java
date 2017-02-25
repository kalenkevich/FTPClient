package ftp.client.FTPClient.file.parser.list;

import ftp.client.FTPClient.file.FTPFile;

import static ftp.client.FTPClient.Config.WINDOWS_FILE_STRUCTURE_REGEX;

/**
 * Created by a.kalenkevich on 22.02.2017.
 */
public class WindowsFTPListFileParser extends AbstractFTPListFileParser {
    public WindowsFTPListFileParser(String currentPathName) {
        super(WINDOWS_FILE_STRUCTURE_REGEX, currentPathName);
    }

    @Override
    protected FTPFile parseEntry(String entry) {
        return null;
    }
}