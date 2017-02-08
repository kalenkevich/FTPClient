package sample.component.file.manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.window.controller.Controller;

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
    private List<TableColumn> tableColumns;
    @FXML
    private TableView table;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileItems = new ArrayList<FileItem>();
    }

    public void setFileItems(List<FileItem> items) {
        fileItems = items;
        updateTableView();
    }

    public void setDirectory(String directoryName) {
        currentDirectoryName = directoryName;
        updateTableView();
    }

    private void addColumn(String name, double width) {
        TableColumn<String, String> tableColumn = new TableColumn<>();
        tableColumn.setCellValueFactory(new PropertyValueFactory<>(name));
        tableColumn.setPrefWidth(width);
        tableColumns.add(tableColumn);
    }

    private void updateTableView() {
        ObservableList<FileItem> observableList = FXCollections.observableList(fileItems);
        onDataChanged(observableList);
    }

    private void onDataChanged(ObservableList<FileItem> data) {
        table.setItems(data);
        table.refresh();
    }
}