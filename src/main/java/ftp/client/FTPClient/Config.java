package ftp.client.FTPClient;

/**
 * Created by a.kalenkevich on 22.02.2017.
 */
public interface Config {
    String UNIX_FILE_STRUCTURE_REGEX = "([bcdelfmpSs-])(((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-])))\\+?\\s+(\\d+)\\s+(?:(\\S+(?:\\s\\S+)*?)\\s+)?(?:(\\S+(?:\\s\\S+)*)\\s+)?(\\d+(?:,\\s*\\d+)?)\\s+((?:\\d+[-/]\\d+[-/]\\d+)|(?:\\S+\\s+\\S+))\\s+(\\d+(?::\\d+)?)\\s+(\\S*)(\\s*.*)";
    String WINDOWS_FILE_STRUCTURE_REGEX = "(\\S+)\\s+(\\S+)\\s+(?:(<DIR>)|([0-9]+))\\s+(\\S.*)";
}
