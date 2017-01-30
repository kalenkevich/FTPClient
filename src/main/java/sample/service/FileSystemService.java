package sample.service;

/**
 * Created by alex on 1/29/2017.
 */
public class FileSystemService {
    private static FileSystemService ourInstance = new FileSystemService();

    public static FileSystemService getInstance() {
        return ourInstance;
    }

    private FileSystemService() {
    }
}
