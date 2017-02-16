package ftp.client.component.file;

import javafx.scene.image.Image;

import java.io.File;

/**
 * Created by a.kalenkevich on 16.02.2017.
 */
public class LocalFileItem implements FileItem {
    private Image image;
    private String description;
    private File file;

    public LocalFileItem(File file) {
        this.file = file;
        this.image = file.isDirectory() ? directoryIcon : fileIcon;
        this.description = getFileDescription();
    }

    private String getFileDescription() {
        return (file.isDirectory() ? "Directory" : "File") +
                ", " + (file.canRead() ? "can Read" : "") +
                ", " + (file.canWrite() ? "can Write" : "");
    }

    public Image getImage() {
        return image;
    }

    public String getName() {
        return file.getName();
    }

    public String getPath() {
        return file.getPath();
    }

    public void rename(String name) {
        file.renameTo(new File(name));
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
