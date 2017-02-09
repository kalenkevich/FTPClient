package sample.component.file.service;

import org.apache.log4j.Logger;
import sample.component.file.FileItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
                    files.add(new FileItem(file));
                }
            }
        } catch (IOException e) {
            logger.error(e);
        }

        return files;
    }
}