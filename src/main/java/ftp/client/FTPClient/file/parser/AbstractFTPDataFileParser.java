package ftp.client.FTPClient.file.parser;

import ftp.client.FTPClient.file.FTPFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by a.kalenkevich on 22.02.2017.
 */
public abstract class AbstractFTPDataFileParser implements FTPDataFileParser {
    private Pattern pattern;
    private Matcher matcher;
    private MatchResult result;
    protected String currentPathName;

    @Override
    public List<FTPFile> parse(List<String> entries) {
        List<FTPFile> files = new ArrayList<>();

        for (String entry: entries) {
            FTPFile ftpFile = parseEntry(entry);
            files.add(ftpFile);
        }

        return files;
    }

    protected abstract FTPFile parseEntry(String entry);

    public AbstractFTPDataFileParser(String regex, String currentPathName) {
        pattern = Pattern.compile(regex);
        this.currentPathName = currentPathName;
    }

    protected boolean isValidEntry(String entry) {
        result = null;
        matcher = pattern.matcher(entry);
        boolean isMatches = matcher.matches();

        if (isMatches) {
            result = matcher.toMatchResult();
        }

        return result != null;
    }

    protected String group(int matchnum) {
        return result == null ? null : result.group(matchnum);
    }
}