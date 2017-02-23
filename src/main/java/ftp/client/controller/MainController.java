package ftp.client.controller;

import ftp.client.component.tab.TabWindowController;
import ftp.client.service.RouterService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import java.net.URL;
import java.util.ResourceBundle;

import static ftp.client.config.Consts.TAB_VIEW;

public class MainController implements Initializable {

    @FXML
    private TabPane tabPane;

    public void initialize(URL location, ResourceBundle resources) {
        newSessionAction();
    }

    @FXML
    public void newSessionAction() {
        createNewTab();
    }

    private void createNewTab() {
        Pane pane = (Pane) RouterService.getInstance().getView(TAB_VIEW);
        TabWindowController tabWindowController = (TabWindowController) RouterService.getInstance().getController();
        Tab tab = new Tab();
        tabWindowController.init();

        tab.setId(tabWindowController.getName());
        tab.setContent(pane);
        tabPane.getTabs().add(tab);
    }
}