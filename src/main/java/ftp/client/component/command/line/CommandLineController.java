package ftp.client.component.command.line;

import ftp.client.FTPClient.FTPClient;
import ftp.client.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by alex on 2/8/2017.
 */
public class CommandLineController implements Controller {
    @FXML
    private TextField commandFiled;
    private FTPClient ftpClient;

    @Override
    public void init() {

    }

    @FXML
    public void sendAction() {
        String command = commandFiled.getText().toUpperCase();
        if (!command.isEmpty()) {
            //ftpClient.sendCommand(command);
            commandFiled.setText("");
        }
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
