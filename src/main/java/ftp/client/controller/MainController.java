package ftp.client.controller;

import ftp.client.service.RouterService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import org.apache.log4j.Logger;
import java.net.URL;
import java.util.ResourceBundle;

import static ftp.client.config.Consts.TAB_VIEW;

public class MainController implements Controller {

    @FXML
    private TabPane tabPane;
    private Logger logger = Logger.getLogger(MainController.class);

    public void initialize(URL location, ResourceBundle resources) {
        newSessionAction();
    }

    @FXML
    public void newSessionAction() {
        createNewTab();
    }

    private void createNewTab() {
        Pane pane = (Pane) RouterService.getInstance().getView(TAB_VIEW);
        Tab tab = new Tab();
        tab.setContent(pane);
        tabPane.getTabs().add(tab);
    }
}