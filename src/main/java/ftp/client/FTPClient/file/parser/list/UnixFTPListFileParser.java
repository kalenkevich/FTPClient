package ftp.client.FTPClient.file.parser.list;

import ftp.client.FTPClient.file.FTPFile;

import static ftp.client.FTPClient.Config.UNIX_FILE_STRUCTURE_REGEX;

/**
 * Created by a.kalenkevich on 17.02.2017.
 */

public class UnixFTPListFileParser extends AbstractFTPListFileParser {
    public UnixFTPListFileParser(String currentPathName) {
        super(UNIX_FILE_STRUCTURE_REGEX, currentPathName);
    }

    @Override
    protected FTPFile parseEntry(String entry) {
        FTPFile ftpFile = null;
        boolean isValidEntry = isValidEntry(entry);

        if (isValidEntry) {
            boolean isDirectory = this.group(1).charAt(0) == 'd';
            String hardLinkCount = this.group(15);
            String usr = this.group(16);
            String grp = this.group(17);
            String size = this.group(18);
            String datestr = this.group(19) + " " + this.group(20);
            String name = this.group(21);
            String endtoken = this.group(22);
            String path = this.currentPathName + '/' + name;

            ftpFile = new FTPFile(path);
            ftpFile.setName(name);
            ftpFile.setDirectory(isDirectory);
            ftpFile.setDescription(size);
        }

        return ftpFile;
    }
}