package ftp.client.component.popup.download;

import ftp.client.controller.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

/**
 * Created by alex on 3/10/2017.
 */
public class DownloadPopupController implements Controller {
    @FXML
    public ProgressBar progressBar;
    @FXML
    public Label fileNameLabel;
    @FXML
    public Label fileSizeLabel;

    @Override
    public void init() {

    }

    @FXML
    public void onCancelAction(ActionEvent actionEvent) {

    }
}
