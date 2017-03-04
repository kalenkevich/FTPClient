package ftp.client.FTPClient.file.parser.list.engine;

import ftp.client.FTPClient.file.FTPFile;
import ftp.client.FTPClient.file.parser.list.FTPListFileParser;
import ftp.client.FTPClient.file.parser.list.UnixFTPListFileParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by a.kalenkevich on 22.02.2017.
 */
public class FTPListFileParserEngine {
    private InputStream inputStream;
    private FTPListFileParser ftpListFileParser;
    private String currentPathName;

    public FTPListFileParserEngine(InputStream inputStream, String currentPathName) {
        this.inputStream = inputStream;
        this.currentPathName = currentPathName;

        initializeFTPFileDataParser();
    }

    //TODO IMPLEMENT FACTORY
    private void initializeFTPFileDataParser() {
        ftpListFileParser = new UnixFTPListFileParser(currentPathName);
    }

    public List<FTPFile> getFiles() throws IOException {
        List<String> entries = getEntries();

        return ftpListFileParser.parse(entries);
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