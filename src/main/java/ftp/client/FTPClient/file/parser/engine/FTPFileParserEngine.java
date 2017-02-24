package ftp.client.FTPClient.file.parser.engine;

import ftp.client.FTPClient.file.FTPFile;
import ftp.client.FTPClient.file.parser.FTPDataFileParser;
import ftp.client.FTPClient.file.parser.UnixFTPDataFileParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static ftp.client.FTPClient.Config.DATA_TYPE_TEXT;

/**
 * Created by a.kalenkevich on 22.02.2017.
 */
public class FTPFileParserEngine {
    private InputStream inputStream;
    private FTPDataFileParser ftpDataFileParser;
    private String currentPathName;

    public FTPFileParserEngine(InputStream inputStream) {
        this(inputStream, DATA_TYPE_TEXT);
    }

    public FTPFileParserEngine(InputStream inputStream, String currentPathName) {
        this.inputStream = inputStream;
        this.currentPathName = currentPathName;

        initializeFTPFileDataParser();
    }

    //TODO IMPLEMENT FACTORY
    private void initializeFTPFileDataParser() {
        ftpDataFileParser = new UnixFTPDataFileParser(currentPathName);
    }

    public List<FTPFile> getFiles() throws IOException {
        List<String> entries = getEntries();

        return ftpDataFileParser.parse(entries);
    }

    private List<String> getEntries() throws IOException {
        BufferedReader stringReader =  new BufferedReader(new InputStreamReader(inputStream));
        List<String> entries = new ArrayList<>();

        for (String line = stringReader.readLine(); line != null; line = stringReader.readLine()) {
            entries.add(line);
        }

        return entries;
    }
}