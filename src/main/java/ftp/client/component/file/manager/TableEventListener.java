package ftp.client.component.file.manager;

import java.io.File;
import java.util.List;

/**
 * Created by a.kalenkevich on 09.02.2017.
 */
public interface TableEventListener {
    void onDrag(Object draggedItem, FileManagerController fileManagerController);
    void onDragExited(Object droppedItem, FileManagerController fileManagerController);
    void onDragEntered(Object droppedItem, FileManagerController fileManagerController);
    void onSelect(List<Object> droppedItems, FileManagerController fileManagerController);
    void onCut(List<Object> droppedItems, FileManagerController fileManagerController);
    void onPaste(List<Object> droppedItems, FileManagerController fileManagerController);
    void onCopy(List<Object> droppedItems, FileManagerController fileManagerController);
    List<Object> getItemsToCopy();
    FileManagerController getCopyFileManagerController();
}
