package ftp.client.component.file;

import javafx.scene.image.Image;

/**
 * Created by a.kalenkevich on 16.02.2017.
 */
public class RemoteFileItem implements FileItem {
    @Override
    public Image getImage() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    public String getPath() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
