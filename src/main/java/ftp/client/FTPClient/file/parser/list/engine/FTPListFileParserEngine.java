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

    /**
     * InputStream from will be data reads.
     */
    private InputStream inputStream;

    /**
     * One of the implementations of the FTPListFileParser.
     */
    private FTPListFileParser ftpListFileParser;

    /**
     * Full path of the directory from which will be parsed FTPFiles.
     */
    private String currentPathName;

    public FTPListFileParserEngine(InputStream inputStream, String currentPathName) {
        this.inputStream = inputStream;
        this.currentPathName = currentPathName;

        initializeFTPFileDataParser();
    }

    /**
     * Initialize all needed object.
     */
    private void initializeFTPFileDataParser() {
        ftpListFileParser = new UnixFTPListFileParser(currentPathName);
    }

    public List<FTPFile> getFiles() throws IOException {
        List<String> entries = getEntries();

        return ftpListFileParser.parse(entries);
    }

    /**
     * API function. Can be invoked after creating of the new instance of FTPListFileParserEngine.
     * Parse data from stream and then return list of parsed FTPFiles.
     * @return list of FTPFiles.
     */
    private List<String> getEntries() throws IOException {
        BufferedReader stringReader =  new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        List<String> entries = new ArrayList<>();

        for (String line = stringReader.readLine(); line != null; line = stringReader.readLine()) {
            entries.add(line);
        }

        return entries;
    }
}