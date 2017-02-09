package ftp.client.component.file.manager;

/**
 * Created by a.kalenkevich on 09.02.2017.
 */
public interface DragAndDropListener {
    void onDrag(Object draggedItem, FileManagerController fileManagerController);
    void onDragExited(Object droppedItem, FileManagerController fileManagerController);
    void onDragEntered(Object droppedItem, FileManagerController fileManagerController);
}
