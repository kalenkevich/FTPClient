package ftp.client.component.file.service;

import ftp.client.component.file.FileItem;
import ftp.client.component.file.LocalFileItem;
import ftp.client.component.file.SpecialFileItem;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by a.kalenkevich on 08.02.2017.
 */
public class LocalFileSystemService implements FileSystemService {
    private Logger logger = Logger.getLogger(LocalFileSystemService.class);
    @Override
    public List<FileItem> getFilesFromDirectory(String directoryName) {
        List<FileItem> files = new ArrayList<>();
        DirectoryStream<Path> stream;
        try {
            stream = Files.newDirectoryStream(Paths.get(directoryName));
            for (Path path: stream) {
                File file = path.toFile();
                if (!file.isHidden()) {
                    files.add(new LocalFileItem(file));
                }
            }

            FileItem rootFile = getRootFileItem(directoryName);
            files.add(0, rootFile);
        } catch (IOException e) {
            logger.error(e);
        }

        return files;
    }

    @Override
    public CompletableFuture<List<FileItem>> getFilesFromDirectoryAsync(String directoryName) {
        return CompletableFuture.completedFuture(getFilesFromDirectory(directoryName));
    }

    @Override
    public void renameFile(FileItem fileItem, String newName) {
        File file = null;
        try {
            file = getFile(fileItem, null);
        } catch (Exception e) {
            logger.error(e);
        }
        if (file.isFile()) {
            int nameLength = file.getName().length();
            int pathLength = file.getPath().length();
            String newFilePathWithoutName = file.getPath().substring(0, pathLength - nameLength);
            String newFileName = newFilePathWithoutName + newName;

            file.renameTo(new File(newFileName));
        }
    }

    @Override
    public void deleteFile(FileItem fileItem) {
        File file = null;
        try {
            file = getFile(fileItem, null);
        } catch (Exception e) {
            logger.error(e);
        }
        if (file.isFile()) {
            file.delete();
        }
    }

    @Override
    public void addFile(File file) {
        FileItem fileItem = new LocalFileItem(file.getAbsoluteFile());
        try {
            file = fileItem.getFile();
        } catch (Exception e) {
            logger.error(e);
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public CompletableFuture<Boolean> addFileAsync(File file) {
        addFile(file);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public FileItem getRootFileItem(String path) {
        File file = new File(path);
        File parentFile = file.getParentFile();
        if (parentFile != null) {
            return new SpecialFileItem(parentFile.getPath());
        }

        return new LocalFileItem(file);
    }

    @Override
    public File getFile(FileItem fileItem, String localFilePath) {
        if (fileItem instanceof LocalFileItem) {
            return ((LocalFileItem)fileItem).getFile();
        }

        return null;
    }

    @Override
    public CompletableFuture<File> getFileAsync(FileItem fileItem, String localFilePath) {
        File file = getFile(fileItem, localFilePath);

        return CompletableFuture.completedFuture(file);
    }
}