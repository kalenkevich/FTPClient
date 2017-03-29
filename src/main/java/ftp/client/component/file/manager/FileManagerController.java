package ftp.client.component.file.manager;

import ftp.client.component.file.FileItem;
import ftp.client.component.file.service.FileSystemService;
import ftp.client.controller.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by a.kalenkevich on 02.02.2017.
 */
public class FileManagerController implements Controller {
    public TextField navigationInput;
    private String currentDirectoryPath = "/";
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
        tableEventListeners = new ArrayList<>();
        selectedItems = new ArrayList<Object>();
        initTable();
        initColumns();
        initEvents();
        updateButtonsDisabledState();
        updatePasteButtonDisabledState(true);
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
            } else if (event.isControlDown() && event.getCode() == KeyCode.C) {
                notifyAboutCopyAction(table.getSelectionModel().getSelectedItems());
            } else if (event.isControlDown() && event.getCode() == KeyCode.V) {
                pasteAction();
            } else if (event.isControlDown() && event.getCode() == KeyCode.X) {
                notifyAboutCutAction(table.getSelectionModel().getSelectedItems());
            } else if (event.getCode() == KeyCode.ENTER) {
                changeDirectory(selectedObject);
            } else if (event.getCode() == KeyCode.BACK_SPACE) {
                changeDirectory(table.getItems().get(0));
            }
        });
        table.setOnDragDetected(event -> {
            notifyAboutDragAction(table.getSelectionModel().getSelectedItem());
        });
    }

    private void changeDirectory(Object selectedItem) {
        FileItem fileItem = (FileItem) selectedItem;
        if (fileItem.isDirectory()) {
            setDirectoryPath(fileItem.getPath());
        }
    }

    private void changeName(Object selectedItem, String newFileName) {
        FileItem fileItem = (FileItem) selectedItem;
        fileSystemService.renameFile(fileItem, currentDirectoryPath + "/" + newFileName);
        update();
    }

    private void deleteFile(Object selectedItem) {
        FileItem fileItem = (FileItem) selectedItem;
        fileSystemService.deleteFile(fileItem);
        update();
    }

    private void addFile(Object selectedItem, FileSystemService remoteFileSystemService) {
        FileItem fileItem = (FileItem) selectedItem;
        String localFilePath = getFilePath(fileItem.getName());

        remoteFileSystemService.getFileAsync(fileItem, localFilePath).thenAccept(
                file -> fileSystemService.addFileAsync(file).thenRun(this::update)
        );
    }

    private String getFilePath(String name) {
        if (!currentDirectoryPath.equals("/")) {
            return currentDirectoryPath + "/" + name;
        }

        return currentDirectoryPath + name;
    }

    private void updateButtonsDisabledState() {
        boolean disabledState = true;
        if (selectedItems.size() != 0) {
            disabledState = false;
        }
        cutButton.setDisable(disabledState);
        copyButton.setDisable(disabledState);
        deleteButton.setDisable(disabledState);
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

    public void setDirectoryPath(String directoryName) {
        currentDirectoryPath = directoryName;
        navigationInput.setText(currentDirectoryPath);
        update();
    }

    @FXML
    public void goAction() {
        String directoryPath = navigationInput.getText();
        setDirectoryPath(directoryPath);
    }

    public void update() {
        updateFileItems();
    }

    private synchronized void updateFileItems() {
        changeGridData(FXCollections.observableArrayList(fileSystemService.getFilesFromDirectory(currentDirectoryPath)));
    }

    private void changeGridData(ObservableList<FileItem> data) {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                table.itemsProperty().setValue(data);
                table.refresh();
            }
        }, 250);
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

    public void pasteAction() {
        List<Object> objectsToPaste = new ArrayList<>();
        for (TableEventListener tableEventListener: tableEventListeners) {
            List<Object> objects = tableEventListener.getItemsToCopy();
            for (Object object: objects) {
                objectsToPaste.add(object);
            }
        }

        FileSystemService fileSystemService = tableEventListeners.get(0).getCopyFileManagerController().getFileSystemService();

        for (Object object: objectsToPaste) {
            addFile(object, fileSystemService);
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