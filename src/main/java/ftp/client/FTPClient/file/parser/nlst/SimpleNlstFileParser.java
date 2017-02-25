package ftp.client.FTPClient.file.parser.nlst;

import ftp.client.FTPClient.file.FTPFile;

import java.io.InputStream;
import java.util.List;

/**
 * Created by alex on 2/25/2017.
 */

//todo implement
public class SimpleNlstFileParser implements FTPNlstFileParser {
    private InputStream inputStream;
    private String currentPathName;
    public SimpleNlstFileParser(InputStream inputStream, String currentPathName) {
        this.inputStream = inputStream;
        this.currentPathName = currentPathName;
    }

    @Override
    public List<FTPFile> parse(List<String> entries) {
        return null;
    }
}
