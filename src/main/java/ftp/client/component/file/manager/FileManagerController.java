package ftp.client.component.file.manager;

import ftp.client.component.file.FileItem;
import ftp.client.component.file.service.FileSystemService;
import ftp.client.controller.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by a.kalenkevich on 02.02.2017.
 */
public class FileManagerController implements Controller {
    private String currentDirectoryName = "/";
    private List<FileItem> fileItems;
    private FileSystemService fileSystemService;
    private List<TableEventListener> tableEventListeners;
    private List<Object> selectedItems;

    @FXML
    private TableView table;
    @FXML
    private Button cutButton;
    @FXML
    private Button copyButton;
    @FXML
    private Button pasteButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button refreshButton;
    
    @Override
    public void init() {
        fileItems = new ArrayList<>();
        tableEventListeners = new ArrayList<>();
        selectedItems = new ArrayList();
        initTable();
        initColumns();
        initEvents();
        updateButtonsDisabledState();
    }

    private void initTable() {
        table.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );
        table.setEditable(true);
    }

    private void initColumns() {
        addIconColumn();
        addNameColumn();
        addDescriptionColumn();
        table.refresh();
    }

    private void addIconColumn() {
        TableColumn<FileItem, Image> tableColumn = new TableColumn<>();
        tableColumn.setCellFactory(param -> new TableCell<FileItem, Image>() {
            @Override
            protected void updateItem(Image item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    ImageView imageview = new ImageView();
                    imageview.setFitHeight(25);
                    imageview.setFitWidth(25);
                    imageview.setImage(item);

                    setGraphic(imageview);
                }
            }
        });
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
        tableColumn.setPrefWidth(30);
        table.getColumns().add(tableColumn);
    }

    private void addNameColumn() {
        TableColumn<FileItem, String> fileNameColumn = new TableColumn<>();
        fileNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name"));
        fileNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        fileNameColumn.setOnEditCommit(
                handler -> changeName(handler.getTableView().getItems().get(handler.getTablePosition().getRow()), handler.getNewValue())
        );
        fileNameColumn.setPrefWidth(200);
        table.getColumns().add(fileNameColumn);
    }

    private void addDescriptionColumn() {
        addColumn("description", 400);
    }

    private void addColumn(String name, double width) {
        TableColumn<String, String> tableColumn = new TableColumn<>();
        tableColumn.setCellValueFactory(new PropertyValueFactory<>(name));
        tableColumn.setPrefWidth(width);
        table.getColumns().add(tableColumn);
        table.refresh();
    }

    private void initEvents() {
        table.setOnMousePressed(event -> {
            int clickCount = event.getClickCount();
            Object selectedObject = table.getSelectionModel().getSelectedItem();
            if (event.isPrimaryButtonDown()) {
                if (clickCount == 1) {
                    notifyAboutSelectAction(new ArrayList<>(table.getSelectionModel().getSelectedItems()));
                } else if (clickCount == 2) {
                    changeDirectory(selectedObject);
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
        if (fileItem.isDirectory()) {
            setDirectory(fileItem.getPath());
        }
    }

    private void changeName(Object selectedItem, String newFileName) {
        FileItem fileItem = (FileItem) selectedItem;
        fileSystemService.renameFile(fileItem, newFileName);
        update();
    }

    private void deleteFile(Object selectedItem) {
        FileItem fileItem = (FileItem) selectedItem;
        fileSystemService.deleteFile(fileItem);
        update();
    }

    private void addFile(Object selectedItem) {
        FileItem fileItem = (FileItem) selectedItem;
        fileSystemService.addFile(fileItem);
        update();
    }

    private void updateButtonsDisabledState() {
        boolean disabledState = true;
        if (selectedItems.size() != 0) {
            disabledState = false;
        }
        cutButton.setDisable(disabledState);
        copyButton.setDisable(disabledState);
        deleteButton.setDisable(disabledState);
        updatePasteButtonDisabledState(true);
    }

    public void updatePasteButtonDisabledState(boolean state) {
        pasteButton.setDisable(state);
    }

    private void notifyAboutSelectAction(List<Object> selectedItems) {
        this.selectedItems = selectedItems;
        updateButtonsDisabledState();

        for (TableEventListener tableEventListener : tableEventListeners) {
            tableEventListener.onSelect(selectedItems, this);
        }
    }

    private void notifyAboutDragAction(Object selectedItem) {
        for (TableEventListener tableEventListener : tableEventListeners) {
            tableEventListener.onDrag(selectedItem, this);
        }
    }

    private void notifyAboutCopyAction(List<Object> selectedItems) {
        for (TableEventListener tableEventListener : tableEventListeners) {
            tableEventListener.onCopy(selectedItems, this);
        }
    }

    private void notifyAboutCutAction(List<Object> selectedItems) {
        for (TableEventListener tableEventListener : tableEventListeners) {
            tableEventListener.onCut(selectedItems, this);
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

    public void addDragAndDropListener(TableEventListener tableEventListener) {
        this.tableEventListeners.add(tableEventListener);
    }

    public void cutAction(ActionEvent actionEvent) {
        notifyAboutCutAction(selectedItems);
    }

    public void copyAction(ActionEvent actionEvent) {
        notifyAboutCopyAction(selectedItems);
    }

    public void pasteAction(ActionEvent actionEvent) {
        List<Object> objectsToPaste = new ArrayList<>();
        for (TableEventListener tableEventListener: tableEventListeners) {
            List<Object> objects = tableEventListener.getItemsToCopy();
            for (Object object: objects) {
                objectsToPaste.add(object);
            }
        }

        for (Object object: objectsToPaste) {
            addFile(object);
        }
    }

    public void deleteAction(ActionEvent actionEvent) {
        for (Object selectedItem: selectedItems) {
            deleteFile(selectedItem);
        }
    }

    public void refreshAction(ActionEvent actionEvent) {
        update();
    }
}