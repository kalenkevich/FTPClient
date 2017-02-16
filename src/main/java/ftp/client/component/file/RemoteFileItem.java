package ftp.client.component.file;

import ftp.client.FTPClient.file.FTPFile;
import javafx.scene.image.Image;

/**
 * Created by a.kalenkevich on 16.02.2017.
 */
public class RemoteFileItem implements FileItem {
    private FTPFile ftpFile;
    private Image image;

    public RemoteFileItem(FTPFile ftpFile) {
        this.ftpFile = ftpFile;
        this.image = ftpFile.isDirectory() ? directoryIcon : fileIcon;
    }

    @Override
    public Image getImage() {
        return null;
    }

    @Override
    public String getName() {
        return ftpFile.getName();
    }

    public String getPath() {
        return ftpFile.getPath();
    }

    @Override
    public void rename(String name) {
        ftpFile.rename(name);
    }

    @Override
    public boolean isDirectory() {
        return ftpFile.isDirectory();
    }

    @Override
    public String getDescription() {
        return ftpFile.getDescription();
    }

    public FTPFile getFTPFile() {
        return ftpFile;
    }
}
