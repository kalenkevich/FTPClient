package sample.component.file.manager;

import javax.swing.*;

/**
 * Created by a.kalenkevich on 02.02.2017.
 */
public class FileItem {
    private String name;
    private ImageIcon icon;
    private String description;

    public FileItem(String name, ImageIcon icon, String description) {
        this.name = name;
        this.icon = icon;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}