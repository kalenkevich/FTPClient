package ftp.client.component.file;

import javafx.scene.image.Image;

/**
 * Created by a.kalenkevich on 16.02.2017.
 */
public class SpecialFileItem implements FileItem {
    private String name;
    private String rootPath;
    private static final String PREFIX = "Back to ";
    public SpecialFileItem(String path) {
        this.rootPath = path;
        this.name = PREFIX + path;
    }

    @Override
    public Image getImage() {
        return directoryIcon;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getPath() {
        return rootPath;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public String getDescription() {
        return "";
    }
}
