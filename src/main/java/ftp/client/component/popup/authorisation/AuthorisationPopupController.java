package ftp.client.component.popup.authorisation;

import ftp.client.FTPClient.FTPClient;
import ftp.client.FTPClient.SimpleFTPClient;
import ftp.client.FTPClient.report.FTPConnectionReport;
import ftp.client.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ftp.client.user.User;

/**
 * Created by alex on 1/29/2017.
 */
public class AuthorisationPopupController implements Controller {

    @FXML
    private TextField hostField;

    @FXML
    private TextField portField;

    @FXML
    private TextField loginField;

    //todo change to PasswordField
    @FXML
    private TextField passField;

    @FXML
    private ImageView connectionStatusImage;

    @FXML
    private Label messageLabel;
    @FXML
    private Button loginButton;

    private Stage dialogStage;

    private static Image successImage = new Image(String.valueOf(AuthorisationPopupController.class.getClassLoader().getResource("icon/success.png")));
    private static Image errorImage = new Image(String.valueOf(AuthorisationPopupController.class.getClassLoader().getResource("icon/error.png")));
    private static String ERROR_CLASS = "error";

    @Override
    public void init() {

    }

    public void onSubmit() {
        boolean testConnectionSuccessful = testConnection();
        if (testConnectionSuccessful) {
            this.dialogStage.close();
        }
    }

    public TextField getLoginField() {
        return loginField;
    }

    public void setLoginField(TextField loginField) {
        this.loginField = loginField;
    }

    public TextField getPassField() {
        return passField;
    }

    public void setPassField(PasswordField passField) {
        this.passField = passField;
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public String getLogin() {
        return this.getLoginField().getText();
    }

    public String getPassword() {
        return this.getPassField().getText();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public TextField getHostField() {
        return hostField;
    }

    public void setHostField(TextField hostField) {
        this.hostField = hostField;
    }

    public TextField getPortField() {
        return portField;
    }

    public void setPortField(TextField portField) {
        this.portField = portField;
    }

    public User getUser() {
        return new User(this.getHostField().getText(), Integer.parseInt(this.getPortField().getText()), this.getLogin(), this.getPassword());
    }

    @FXML
    public boolean testConnection() {
        User user = getUser();
        FTPClient ftpClient = new SimpleFTPClient();
        FTPConnectionReport connectionReport = ftpClient.testConnection(user.getHostName(), user.getPort(), user.getName(), user.getPassword());
        boolean fullySuccess = connectionReport.isFullySuccess();

        showErrorMessage("");
        setNormalBorderToTextFields();
        enableLoginButton();

        if (fullySuccess) {
            connectionStatusImage.setImage(successImage);
        } else {
            connectionStatusImage.setImage(errorImage);
            disableLoginButton();
            if (!connectionReport.isSuccessConnection()) {
                showErrorMessage("Host or port are not valid");
                setErrorBorder(hostField);
                setErrorBorder(portField);
            } else if (!connectionReport.isSuccessUserLogin()) {
                showErrorMessage("User name or password are not valid");
                setErrorBorder(loginField);
                setErrorBorder(passField);
            }
        }

        return fullySuccess;
    }

    private void setNormalBorderToTextFields() {
        hostField.getStyleClass().remove(ERROR_CLASS);
        portField.getStyleClass().remove(ERROR_CLASS);
        loginField.getStyleClass().remove(ERROR_CLASS);
        passField.getStyleClass().remove(ERROR_CLASS);
    }

    private void setErrorBorder(TextField textField) {
        textField.getStyleClass().add(ERROR_CLASS);
    }

    private void disableLoginButton() {
        loginButton.setDisable(true);
    }

    private void enableLoginButton() {
        loginButton.setDisable(false);
    }

    private void showErrorMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setTextFill(Color.RED);
    }
}