package sample.component.file;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;

/**
 * Created by a.kalenkevich on 08.02.2017.
 */
public class FileItem {
    private Icon icon;
    private String name;
    private String description;
    private Icon directoryIcon = new ImageIcon(getClass().getClassLoader().getResource("icon/dir.png"));
    private Icon fileIcon = new ImageIcon(getClass().getClassLoader().getResource("icon/file.png"));
    private static Logger logger = Logger.getLogger(FileItem.class);
    private File file;

    public FileItem(File file) {
        this.file = file;
        this.icon = generateIcon();
        this.name = file.getName();
        this.description = getFileDescription();
    }

    private String getFileDescription() {
        return (file.isDirectory() ? "Directory" : "File") +
                ", " + (file.canRead() ? "can Read" : "") +
                ", " + (file.canWrite() ? "can Write" : "");
    }

    private Icon generateIcon() {
        return file.isDirectory() ? directoryIcon : fileIcon;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}