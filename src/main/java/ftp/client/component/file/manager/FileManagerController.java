package ftp.client.component.file.manager;

import ftp.client.component.file.FileItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ftp.client.component.file.manager.service.FileSystemService;
import ftp.client.controller.Controller;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by a.kalenkevich on 02.02.2017.
 */
public class FileManagerController implements Initializable, Controller {
    private String currentDirectoryName = "/";
    private List<FileItem> fileItems;
    private FileSystemService fileSystemService;
    private List<DragAndDropListener> dragAndDropListeners;
    @FXML
    private TableView table;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileItems = new ArrayList<>();
        dragAndDropListeners = new ArrayList<>();
        addColumn("name", 200);
        addColumn("description", 400);
        initEvents();
    }

    private void initEvents() {
        table.setOnMousePressed(event -> {
            int clickCount = event.getClickCount();
            Object selectedObject = table.getSelectionModel().getSelectedItem();
            if (event.isPrimaryButtonDown()) {
                if (clickCount == 2) {
                    changeDirectory(selectedObject);
                } else if (clickCount == 3) {
                    changeName(selectedObject);
                }
            }
        });
        table.setOnKeyReleased(event -> {
            Object selectedObject = table.getSelectionModel().getSelectedItem();
            if (event.getCode() == KeyCode.DELETE) {
                deleteFile(selectedObject);
            }
        });
        table.setOnDragDetected(event -> {
            notifyAboutDragAction(table.getSelectionModel().getSelectedItem());
        });
    }

    private void changeDirectory(Object selectedItem) {
        FileItem fileItem = (FileItem) selectedItem;
        File file = fileItem.getFile();
        if (file.isDirectory()) {
            setDirectory(file.getPath());
        }
    }

    private void changeName(Object selectedItem) {
        FileItem fileItem = (FileItem) selectedItem;
        File file = fileItem.getFile();
        //todo implement rename
        String newFileName = "-test";
        fileSystemService.renameFile(file, newFileName);
        update();
    }

    private void deleteFile(Object selectedItem) {
        FileItem fileItem = (FileItem) selectedItem;
        File file = fileItem.getFile();
        fileSystemService.deleteFile(file);
        update();
    }

    private void notifyAboutDragAction(Object selectedItem) {
        for (DragAndDropListener dragAndDropListener: dragAndDropListeners) {
            dragAndDropListener.onDrag(selectedItem, this);
        }
    }

    public void setDirectory(String directoryName) {
        currentDirectoryName = directoryName;
        update();
    }

    public void update() {
        updateFileItems();
        updateTableView();
    }
    private void updateFileItems() {
        fileItems = fileSystemService.getFilesFromDirectory(currentDirectoryName);
        fileItems.add(0, getRootFile());
    }

    private FileItem getRootFile() {
        File file = new File(currentDirectoryName);
        File parentFile = file.getParentFile();
        if (parentFile != null) {
            return new FileItem(parentFile);
        }

        return new FileItem(file);
    }

    //todo new column property factory to use icons.
    private void addColumn(String name, double width) {
        TableColumn<String, String> tableColumn = new TableColumn<>();
        tableColumn.setCellValueFactory(new PropertyValueFactory<>(name));
        tableColumn.setPrefWidth(width);
        table.getColumns().add(tableColumn);
        table.refresh();
    }

    private void updateTableView() {
        ObservableList<FileItem> observableList = FXCollections.observableList(fileItems);
        changeGridData(observableList);
    }

    private void changeGridData(ObservableList<FileItem> data) {
        table.setItems(data);
        table.refresh();
    }

    public FileSystemService getFileSystemService() {
        return fileSystemService;
    }

    public void setFileSystemService(FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;
    }

    public void addDragAndDropListener(DragAndDropListener dragAndDropListener) {
        this.dragAndDropListeners.add(dragAndDropListener);
    }
}