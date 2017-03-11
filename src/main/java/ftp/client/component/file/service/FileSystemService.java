package ftp.client.component.file.service;

import ftp.client.component.file.FileItem;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by a.kalenkevich on 08.02.2017.
 */
public interface FileSystemService {
    List<FileItem> getFilesFromDirectory(String directoryName);
    CompletableFuture<List<FileItem>> getFilesFromDirectoryAsync(String directoryName);
    void addFile(File file);
    CompletableFuture<Boolean> addFileAsync(File file);
    File getFile(FileItem fileItem, String localFilePath);
    CompletableFuture<File> getFileAsync(FileItem fileItem, String localFilePath);
    void renameFile(FileItem file, String name);
    void deleteFile(FileItem file);
    FileItem getRootFileItem(String path);
}
