package ftp.client.component.file.service;

import ftp.client.component.file.FileItem;

import java.io.File;
import java.util.List;

/**
 * Created by a.kalenkevich on 08.02.2017.
 */
public interface FileSystemService {
    List<FileItem> getFilesFromDirectory(String directoryName);
    void renameFile(FileItem file, String name);
    void deleteFile(FileItem file);
    void addFile(File file);
    FileItem getRootFileItem(String path);
    File getFile(FileItem fileItem, String localFilePath);
}
