package ftp.client.FTPClient.file;

import java.io.File;

/**
 * Created by a.kalenkevich on 16.02.2017.
 */
public class FTPFile {

    /**
     * Describes file's name.
     */
    private String name;

    /**
     * File's description.
     */
    private String description;

    /**
     * FTPFile Class describes directories also. So it this flag needed to distinguish them.
     */
    private boolean isDirectory;

    /**
     * Describes file's full path.
     */
    private String path;

    /**
     * Real file implementation.
     */
    private File file;

    public FTPFile(String path) {
        this.path = path;
    }

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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}