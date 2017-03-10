package ftp.client.component.text.buffer;

import ftp.client.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by alex on 2/8/2017.
 */
public class TextBufferController implements Controller {
    @FXML
    private TextArea textArea;

    @Override
    public void init() {
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            textArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    public void clearAction() {
        textArea.setText("");
    }

    public TextArea getTextArea() {
        return textArea;
    }
}
