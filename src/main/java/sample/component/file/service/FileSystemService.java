package sample.component.file.service;

import sample.component.file.FileItem;

import java.util.List;

/**
 * Created by a.kalenkevich on 08.02.2017.
 */
public interface FileSystemService {
    List<FileItem> getFilesFromDirectory(String directoryName);
}
