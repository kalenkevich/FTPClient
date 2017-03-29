package ftp.client.component.file.service;

import ftp.client.component.file.FileItem;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by a.kalenkevich on 08.02.2017.
 */
public interface FileSystemService {
    List<FileItem> getFilesFromDirectory(String directoryName) throws FileException;
    CompletableFuture<List<FileItem>> getFilesFromDirectoryAsync(String directoryName) throws FileException;
    void addFile(File file) throws FileException;
    CompletableFuture addFileAsync(File file) throws FileException;
    File getFile(FileItem fileItem, String localFilePath) throws FileException;
    CompletableFuture<File> getFileAsync(FileItem fileItem, String localFilePath) throws FileException;
    void renameFile(FileItem file, String name) throws FileException;
    void deleteFile(FileItem file) throws FileException;
    FileItem getRootFileItem(String path) throws FileException;
}
