package sample.component.file.manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.component.file.FileItem;
import sample.component.file.service.FileSystemService;
import sample.window.controller.Controller;

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
    @FXML
    private TableView table;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileItems = new ArrayList<>();
        addColumn("name", 200);
        addColumn("description", 400);
        initEvents();
    }

    private void initEvents() {
        table.setOnMousePressed(event -> {
            int clickCount = event.getClickCount();
            if (event.isPrimaryButtonDown()) {
                if (clickCount == 1) {
                    notifyObservers(table.getSelectionModel().getSelectedItem());
                } else if (clickCount == 2) {
                    changeDirectory(table.getSelectionModel().getSelectedItem());
                }
            }
        });
    }

    private void changeDirectory(Object selectedItem) {
        FileItem fileItem = (FileItem) selectedItem;
        File file = fileItem.getFile();
        if (file.isDirectory()) {
            setDirectory(file.getPath());
        }
    }

    private void notifyObservers(Object selectedItem) {

    }

    public void setFileItems(List<FileItem> items) {
        fileItems = items;
        updateTableView();
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
        onDataChanged(observableList);
    }

    private void onDataChanged(ObservableList<FileItem> data) {
        table.setItems(data);
        table.refresh();
    }

    public FileSystemService getFileSystemService() {
        return fileSystemService;
    }

    public void setFileSystemService(FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;
    }
}