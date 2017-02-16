package ftp.client.component.file;

import javafx.scene.image.Image;

import java.io.File;

/**
 * Created by a.kalenkevich on 16.02.2017.
 */
public class LocalFileItem implements FileItem {
    private Image icon;
    private String description;
    private Image directoryIcon = new Image(String.valueOf(getClass().getClassLoader().getResource("icon/dir.png")));
    private Image fileIcon = new Image(String.valueOf(getClass().getClassLoader().getResource("icon/file.png")));
    private File file;

    public LocalFileItem(File file) {
        this.file = file;
        this.icon = generateIcon();
        this.description = getFileDescription();
    }

    private String getFileDescription() {
        return (file.isDirectory() ? "Directory" : "File") +
                ", " + (file.canRead() ? "can Read" : "") +
                ", " + (file.canWrite() ? "can Write" : "");
    }

    private Image generateIcon() {
        return file.isDirectory() ? directoryIcon : fileIcon;
    }

    public Image getIcon() {
        return icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }

    public String getName() {
        return file.getName();
    }

    public String getPath() {
        return file.getPath();
    }

    public void setName(String name) {
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
