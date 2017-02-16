package ftp.client.component.file.manager;

import java.util.List;

/**
 * Created by a.kalenkevich on 09.02.2017.
 */
public interface TableEventListener {
    void onDrag(Object draggedItem, FileManagerController fileManagerController);
    void onDragExited(Object droppedItem, FileManagerController fileManagerController);
    void onDragEntered(Object droppedItem, FileManagerController fileManagerController);
    void onSelect(List<Object> droppedItem, FileManagerController fileManagerController);
}
