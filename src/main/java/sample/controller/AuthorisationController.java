package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.user.User;

import java.security.SecureRandom;

/**
 * Created by alex on 1/29/2017.
 */
public class AuthorisationController implements Controller {
    @FXML
    private TextField loginField;

    //todo change to PasswordField
    @FXML
    private TextField passField;

    private Stage dialogStage;

    public void onSubmit() {
        this.dialogStage.close();
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

    public User getUser() {
        return new User(this.getLogin(), this.getPassword());
    }
}
