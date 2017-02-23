package ftp.client.FTPClient.report;

/**
 * Created by a.kalenkevich on 23.02.2017.
 */
public class FTPConnectionReport {
    private boolean successConnection = false;
    private boolean successUserLogin = false;
    private boolean successCommand = false;
    private boolean successDisconnect = false;

    public boolean isSuccessConnection() {
        return successConnection;
    }

    public void setSuccessConnection(boolean successConnection) {
        this.successConnection = successConnection;
    }

    public boolean isSuccessUserLogin() {
        return successUserLogin;
    }

    public boolean isSuccessCommand() {
        return successCommand;
    }

    public void setSuccessCommand(boolean successCommand) {
        this.successCommand = successCommand;
    }

    public boolean isSuccessDisconnect() {
        return successDisconnect;
    }

    public void setSuccessUserLogin(boolean successLoginUser) {
        this.successUserLogin = successLoginUser;
    }

    public void setSuccessDisconnect(boolean successDisconnect) {
        this.successDisconnect = successDisconnect;
    }

    public boolean isFullySuccess() {
        return successConnection && successUserLogin && successCommand && successDisconnect;
    }
}