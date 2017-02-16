package ftp.client.component.file;

import javafx.scene.image.Image;

/**
 * Created by a.kalenkevich on 08.02.2017.
 */
public interface FileItem {
    Image getIcon();
    String getName();
    String getPath();
    void setName(String name);
    boolean isDirectory();
    String getDescription();
}