package ftp.client.component.file;

import javafx.scene.image.Image;

import java.io.File;

/**
 * Created by a.kalenkevich on 08.02.2017.
 */
public interface FileItem {
    Image directoryIcon = new Image(String.valueOf(FileItem.class.getClassLoader().getResource("icon/dir.png")));
    Image fileIcon = new Image(String.valueOf(FileItem.class.getClassLoader().getResource("icon/file.png")));

    Image getImage();
    String getName();
    String getPath();
    void rename(String name);
    boolean isDirectory();
    String getDescription();
    File getFile();
    void setFile(File file);
}