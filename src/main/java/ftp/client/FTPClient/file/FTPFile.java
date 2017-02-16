package ftp.client.FTPClient.file;

/**
 * Created by a.kalenkevich on 16.02.2017.
 */
public class FTPFile {
    private String name;
    private String description;
    private boolean isDirectory;
    private String path;

    public String getName() {
        return name;
    }

    public void rename(String newName) {

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}